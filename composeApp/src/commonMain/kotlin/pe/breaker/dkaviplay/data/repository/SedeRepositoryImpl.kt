package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import pe.breaker.dkaviplay.data.mapper.toDomain
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.supabase.EmpresaDTO
import pe.breaker.dkaviplay.data.remote.supabase.SedeDTO
import pe.breaker.dkaviplay.data.remote.supabase.view.SedeEmpresaView
import pe.breaker.dkaviplay.data.remote.supabase.view.TarifarioView
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.SedeRepository

class SedeRepositoryImpl(
    private val sessionManager: UserSessionManager,
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository
) : SedeRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSedes(): Flow<Result<List<Sede>>> {
        return sessionManager.getCurrentUsuarioFlow()
            .flatMapLatest { usuario ->
                if (usuario == null) {
                    flowOf(Result.success(emptyList()))
                } else {
                    listenSedesByUbigeo(usuario.departamento, usuario.provincia)
                }
            }
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getSedesByDepartamento(
        departamento: String,
        provincia: String
    ): Result<List<Sede>> {
        return try {
            val sedesViewList = safeSupabaseCall {
                supabaseClient
                    .from(schema = "public", table = "v_SedesEmpresa")
                    .select {
                        filter {
                            eq("departamento", departamento)
                            eq("provincia", provincia)
                        }
                    }.decodeList<SedeEmpresaView>()
            }

            val sedesDomain = sedesViewList.map { viewDto -> viewDto.toDomain() }

            Result.success(sedesDomain)
        } catch (e: Exception) {
            println("Error FetchSedes: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, SupabaseExperimental::class)
    private fun listenSedesByUbigeo(
        idDepartamento: String,
        idProvincia: String
    ): Flow<Result<List<Sede>>> {
        val sedesFlow = supabaseClient
            .from(schema = "public", table = "Sede")
            .selectAsFlow(
                primaryKey = SedeDTO::id,
                channelName = "public:Sede:ubigeo:$idDepartamento:$idProvincia",
                filter = FilterOperation("departamento", FilterOperator.EQ, idDepartamento)
            )
            .mapLatest { listaSedes ->
                listaSedes.filter { it.status && it.provincia == idProvincia }
            }

        // 2. Canal reactivo para las Empresas en general
        // Al usar selectAsFlow sin filtros pesados, mantendrás un caché de empresas sincronizado en RAM
        val empresasFlow = supabaseClient
            .from(schema = "public", table = "Empresa")
            .selectAsFlow(
                primaryKey = EmpresaDTO::id,
                channelName = "public:Empresa:activas"
            )
            .mapLatest { listaEmpresas ->
                listaEmpresas.filter { it.status }.associateBy { it.id }
            }

        // 3. Combinamos ambos flujos reactivos usando 'flatMapLatest' o 'combine'
        return combine(sedesFlow, empresasFlow) { sedesData, empresasMap ->
            try {
                val resultadoDomain = sedesData.mapNotNull { sedeDto ->
                    val empresaDto = empresasMap[sedeDto.idEmpresa]

                    // Si la sede tiene una empresa válida y activa, mapeamos al Dominio
                    empresaDto?.let {
                        Sede(
                            sedeUid = sedeDto.id.toString(),
                            empresaUid = it.id.toString(),
                            nombreSede = sedeDto.nombre ?: "",
                            direccion = sedeDto.direccion ?: "",
                            horario = sedeDto.horario.map { h -> h.toDomain() },
                            numSede = sedeDto.numeroSede ?: "",
                            referencia = sedeDto.referencia ?: "",
                            nombreEmpresa = it.nombre ?: "",
                            logo = it.logo ?: "",
                            ruc = it.ruc ?: "",
                            latitud = sedeDto.latitud?.toDouble() ?:  0.0,
                            longitud = sedeDto.longitud?.toDouble() ?:  0.0
                        )
                    }
                }
                Result.success(resultadoDomain)
            } catch (e: Exception) {
                println("❌ Error procesando snapshot combinado de Supabase: ${e.message}")
                Result.failure(e)
            }
        }
            .distinctUntilChanged()
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getTarifaSede(sedeUid: String): Result<Double?> {
        return try{
            val sedeIdInt = sedeUid.toIntOrNull() ?: 0

            val tarifa = safeSupabaseCall {
                supabaseClient
                    .from(schema = "dkavi", table = "v_tarifario")
                    .select {
                        filter {
                            eq("sede_id", sedeIdInt)
                        }
                    }.decodeSingleOrNull<TarifarioView>()
            }

            if (tarifa == null) {
                println("⚠️ Alerta: No se encontró tarifario activo para la sede con ID: $sedeIdInt")
            }

            Result.success(tarifa?.cantidadMonedasReto)
        }catch (e: Exception) {
            println("❌ Error trayendo tarifa desde Supabase: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getMesasSede(sedeUid: String): String {
        return try {
            val resultado :String = safeSupabaseCall {
                supabaseClient.postgrest.rpc(
                    function = "get_mesas_sede_status",
                    parameters = mapOf(
                        "p_sede_id" to sedeUid.toInt(),
                    )
                ) {
                    schema = "dkavi"
                }.decodeAs()
            }

            resultado
        } catch (e: Exception) {
            println("Error obteniendo estado de mesas: ${e.message}")
            "0/0" // Valor por defecto en caso de error de red
        }
    }

    private suspend fun <T> safeSupabaseCall(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("JWT expired", ignoreCase = true) || errorMsg.contains("PGRST303")) {
                println("🔄 [SedeRepository] Supabase JWT expirado detectado. Intentando autoLogin...")
                val token = sessionManager.getToken()
                if (!token.isNullOrBlank()) {
                    val result = authRepository.autoLogin(token)
                    if (result is AutoLoginResult.Success) {
                        println("✅ [SedeRepository] Sesión refrescada con éxito. Reintentando operación...")
                        return block() // Reintento único
                    }
                }
                throw e
            } else {
                throw e
            }
        }
    }
}