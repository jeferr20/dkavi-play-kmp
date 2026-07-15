package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import pe.breaker.dkaviplay.data.mapper.toDomain
import pe.breaker.dkaviplay.data.remote.supabase.EmpresaDTO
import pe.breaker.dkaviplay.data.remote.supabase.SedeDTO
import pe.breaker.dkaviplay.data.remote.supabase.view.SedeEmpresaView
import pe.breaker.dkaviplay.data.remote.supabase.view.TarifarioView
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.domain.repository.SedeRepository
import kotlin.time.Clock
import kotlin.time.Instant

class SedeRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val sessionManager: UserSessionManager,
    private val supabaseClient: SupabaseClient
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
            val sedesViewList = supabaseClient
                .from(schema = "public", table = "v_SedesEmpresa")
                .select {
                    filter {
                        eq("departamento", departamento)
                        eq("provincia", provincia)
                    }
                }.decodeList<SedeEmpresaView>()

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

            val tarifa = supabaseClient
                .from(schema = "dkavi", table = "v_tarifario")
                .select {
                    filter {
                        eq("sede_id", sedeIdInt)
                    }
                }.decodeSingleOrNull<TarifarioView>()

            if (tarifa == null) {
                println("⚠️ Alerta: No se encontró tarifario activo para la sede con ID: $sedeIdInt")
            }

            Result.success(tarifa?.cantidadMonedasReto)
        }catch (e: Exception) {
            println("❌ Error trayendo tarifa desde Supabase: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getMesasSede(sedeUid: String): Flow<String> = callbackFlow {
        val ahora = Clock.System.now()

        // 1. Query optimizada (Requiere índice compuesto en Firebase)
        val queryReservas = firestore.collection("Reserva")
            .where { "uuidSede" equalTo sedeUid }
            .where { "status" equalTo true }
            .where { "fechaFin" greaterThan ahora.toFirebaseTimestamp() }

        val queryMesas = firestore.collection("Mesa")
            .where { "uuidSede" equalTo sedeUid }
            .where { "status" equalTo true }

        // 2. Usamos combine directamente y enviamos al canal del callbackFlow
        val job =
            combine(queryReservas.snapshots, queryMesas.snapshots) { resSnapshot, mesaSnapshot ->
                val currentInstant = Clock.System.now()

                // Usamos set para búsqueda O(1) más eficiente
                val mesasOcupadasIds = resSnapshot.documents.mapNotNull { doc ->
                    val inicio = doc.get<Timestamp>("fechaInicio")?.toKotlinInstant()
                    val fin = doc.get<Timestamp>("fechaFin")?.toKotlinInstant()
                    val mesaId = doc.get<String>("uuidMesa")

                    if (mesaId != null && inicio != null && fin != null && currentInstant in inicio..fin) {
                        mesaId
                    } else null
                }.toSet()

                val totalMesas = mesaSnapshot.documents.size
                val ocupadasCount = mesasOcupadasIds.size
                val mesasDisponibles = totalMesas - ocupadasCount

                "$mesasDisponibles/$totalMesas"
            }.onEach { result ->
                trySend(result) // Enviamos el String al colector
            }.launchIn(this) // <--- IMPORTANTE: Usa 'this' (el scope del callbackFlow)

        // 3. awaitClose es vital para limpiar recursos
        awaitClose {
            job.cancel()
        }
    }

    fun Timestamp.toKotlinInstant(): Instant {
        return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
    }

    fun Instant.toFirebaseTimestamp(): Timestamp {
        return Timestamp(this.epochSeconds, this.nanosecondsOfSecond)
    }
}