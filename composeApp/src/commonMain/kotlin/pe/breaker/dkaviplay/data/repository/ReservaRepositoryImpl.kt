package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import pe.breaker.dkaviplay.data.mapper.ReservaMapper
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.dto.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.request.RequestAceptarRetoDTO
import pe.breaker.dkaviplay.data.remote.dto.request.RequestUpdateEliminarReservaDTO
import pe.breaker.dkaviplay.data.remote.supabase.rpc.ReservaJuegoDTO
import pe.breaker.dkaviplay.data.remote.supabase.rpc.ReservaRpcDTO
import pe.breaker.dkaviplay.data.remote.supabase.rpc.ValidarHoraReservaDTO
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.ReservaEstado
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.ReservaRepository
import kotlin.random.Random

class ReservaRepositoryImpl(
    private val httpClient: HttpClient,
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager
) : ReservaRepository {

    private val _reservasSharedFlow = MutableSharedFlow<List<Reserva>>(replay = 1)
    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun registroReserva(reserva: RegisterReservaRequestDTO): Result<String> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/reserva") {
            contentType(ContentType.Application.Json)
            setBody(reserva)
        }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun getReservaById(reservaId: String): Result<Reserva> {
        return try {
            val response = safeSupabaseCall {
                supabaseClient.postgrest.rpc(
                    function = "get_reserva_completa",
                    parameters = mapOf(
                        "p_reserva_id" to reservaId.toInt()
                    )
                ) {
                    schema = "dkavi"
                }.decodeAs<ReservaJuegoDTO>()
            }

            println("PARTIDAS = ${response.partidas}")
            val reserva = ReservaMapper.mapJuegoToDomain(response)

            Result.success(reserva)
        } catch (e: Exception) {
            println("Error getReservaById: ${e.message}")
            Result.failure(e)
        }
    }

    override fun getReservasFlow(usuarioUid: String): Flow<List<Reserva>> {
        if (usuarioUid.isEmpty()) return _reservasSharedFlow.asSharedFlow()
        return channelFlow {
            val channelName = "reservas_${usuarioUid}_${Random.nextInt()}"
            val channel = supabaseClient.realtime.channel(channelName)

            // 1. REGISTRAR LOS LISTENERS DE CAMBIOS ANTES DE SUBSTRIBIR
            val flowUser1 = channel.postgresChangeFlow<PostgresAction>(schema = "dkavi") {
                table = "Reserva"
                filter("uuid_user1", FilterOperator.EQ, usuarioUid)
            }
            val flowUser2 = channel.postgresChangeFlow<PostgresAction>(schema = "dkavi") {
                table = "Reserva"
                filter("uuid_user2", FilterOperator.EQ, usuarioUid)
            }

            val changesFlow = merge(flowUser1, flowUser2)

            // 2. CONECTAR AL CANAL DE REALTIME
            channel.subscribe()

            fetchAndEmitRpc(usuarioUid)

            // Función auxiliar para llamar a la RPC y mapear a Dominio
            val realtimeJob = launch {
                changesFlow.collect {
                    fetchAndEmitRpc(usuarioUid)
                }
            }

            // 5. ESCUCHAR EL SHARED FLOW INTERNO Y REDIRIGIR AL COLLECTOR DE LA UI
            val collectorJob = launch {
                _reservasSharedFlow.collect { lista ->
                    send(lista)
                }
            }

            // 6. LIMPIEZA DE RECURSOS AL DESTRUIR EL FLUJO
            awaitClose {
                realtimeJob.cancel()
                collectorJob.cancel()
                launch(Dispatchers.IO) {
                    runCatching { channel.unsubscribe() }
                }
            }
        }
    }

    private suspend fun fetchAndEmitRpc(usuarioUid: String) {
        runCatching {
            safeSupabaseCall {
                val response = supabaseClient.postgrest.rpc(
                    function = "get_reservas_by_user",
                    parameters = mapOf("p_user_uid" to usuarioUid)
                ) {
                    schema = "dkavi"
                }.decodeList<ReservaRpcDTO>()

                response.map { ReservaMapper.mapToDomain(it) }
            }
        }.onSuccess { reservasDomain ->
            _reservasSharedFlow.emit(reservasDomain)
        }.onFailure { error ->
            println("❌ [ReservaRepository] Error ejecutando RPC: ${error.message}")
        }
    }

    // Disparo manual desde el Pull-To-Refresh sin tocar el socket
    override suspend fun refreshReservas(uid: String) {
        if (uid.isNotEmpty()) {
            fetchAndEmitRpc(uid)
        }
    }

    override suspend fun responderReto(reserva: Reserva, aceptar: Boolean): Result<String> {
        return try {
            val nuevoEstadoId = if (aceptar) 5 else 2
            val requestBody = RequestAceptarRetoDTO(
                estadoId = nuevoEstadoId,
                idReserva = reserva.reservaUid.toInt(),
                montoTotal = reserva.montoTotal,
                user1 = reserva.creadorUid
            )
            val response =
                httpClient.put("${ConstatesCloud.URLBASE}apiPublic/public/reserva") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }
            return handleResponse<String, String>(response) { data ->
                Result.success(data)
            }

        } catch (e: Exception) {
            println("Error al responder al reto $reserva.reservaUid.toInt(): ${e.message}")
            Result.failure(Exception("No se pudo actualizar la respuesta del reto: ${e.message}"))
        }
    }

    override suspend fun eliminarReserva(reservaUid: String): Result<String> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/reserva/updateEliminar") {
                contentType(ContentType.Application.Json)
                setBody(
                    RequestUpdateEliminarReservaDTO(
                        idReserva = reservaUid.toInt(),
                        estadoId = -1
                    )
                )
            }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun updateEstadoReserva(
        reservaEstado: ReservaEstado,
        reservaUid: String
    ): Result<String> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/reserva/updateEliminar") {
                contentType(ContentType.Application.Json)
                setBody(
                    RequestUpdateEliminarReservaDTO(
                        idReserva = reservaUid.toInt(),
                        estadoId = reservaEstado.id
                    )
                )
            }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun verificarHoraReserva(reservaId: String): Result<ValidarHoraReservaDTO> {
        return try {
            val response = safeSupabaseCall {
                supabaseClient.postgrest.rpc(
                    function = "validar_hora_inicio_reserva",
                    parameters = buildJsonObject {
                        put("p_reserva_id", reservaId.toInt())
                    }
                ) {
                    schema = "dkavi"
                }.decodeAs<ValidarHoraReservaDTO>()
            }
            Result.success(response)
        } catch (e: Exception) {
            println("Error verificando hora: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun clearCache() {
        _reservasSharedFlow.resetReplayCache()
    }

    private suspend fun <T> safeSupabaseCall(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("JWT expired", ignoreCase = true) || errorMsg.contains("PGRST303")) {
                println("🔄 [ReservaRepository] Supabase JWT expirado detectado. Intentando autoLogin...")
                val token = sessionManager.getToken()
                if (!token.isNullOrBlank()) {
                    val result = authRepository.autoLogin(token)
                    if (result is AutoLoginResult.Success) {
                        println("✅ [ReservaRepository] Sesión refrescada con éxito. Reintentando operación...")
                        return block() // Reintento único
                    }
                }
                // Si no hay token o autoLogin falló, lanzamos el error original
                throw e
            } else {
                throw e
            }
        }
    }
}