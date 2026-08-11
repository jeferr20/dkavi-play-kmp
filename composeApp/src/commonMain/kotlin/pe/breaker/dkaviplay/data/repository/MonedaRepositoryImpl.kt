package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.dto.request.LogMonedaDTO
import pe.breaker.dkaviplay.data.remote.dto.response.ResponseMensajeCompraMonedaDTO
import pe.breaker.dkaviplay.data.remote.supabase.view.TarifarioView
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.MonedaRepository

class MonedaRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val httpClient: HttpClient,
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager
) : MonedaRepository {
    override suspend fun getPrecioMonedas(sedeUid: Int): Result<TarifarioView> {
        return try {
            val tarifa = safeSupabaseCall {
                supabaseClient
                    .from(schema = "dkavi", table = "v_tarifario")
                    .select {
                        filter {
                            eq("sede_id", sedeUid)
                        }
                    }.decodeSingleOrNull<TarifarioView>()
            }

            if (tarifa == null) {
                Result.failure(Exception("No se encontró tarifario activo para la sede con ID: $sedeUid"))
            } else {
                Result.success(tarifa)
            }
        } catch (e: Exception) {
            println("❌ Error trayendo tarifa desde Supabase: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun registroLogMonedas(body: LogMonedaDTO): Result<Unit> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/monedas") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        return handleResponse<String, Unit>(response) { msg ->
            println("Respuesta del server: $msg")
            Result.success(Unit)
        }
    }

    override suspend fun getMensajeWhatsapp(
        userUid: String,
        monedas: Int
    ): Result<String> {
        return try {
            val response = safeSupabaseCall {
                supabaseClient.postgrest.rpc(
                    function = "get_mensaje_compra",
                    parameters = buildJsonObject {
                        put("p_usuario_id", userUid)
                        put("p_monedas", monedas)
                    }
                ) {
                    schema = "public"
                }.decodeAs<ResponseMensajeCompraMonedaDTO>()
            }
            val mensajeCodificado = response.mensaje.encodeURLParameter()
            val celular = response.celular
            if (mensajeCodificado.isEmpty() || celular.isEmpty()) {
                return Result.failure(Exception("No se pudo generar el mensaje"))
            } else {
                val mensaje = "https://wa.me/${celular}?text=$mensajeCodificado"
                return Result.success(mensaje)
            }
        } catch (e: Exception) {
            println("Error generando mensaje: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun <T> safeSupabaseCall(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("JWT expired", ignoreCase = true) || errorMsg.contains("PGRST303")) {
                println("🔄 [MonedaRepository] Supabase JWT expirado detectado. Intentando autoLogin...")
                val token = sessionManager.getToken()
                if (!token.isNullOrBlank()) {
                    val result = authRepository.autoLogin(token)
                    if (result is AutoLoginResult.Success) {
                        println("✅ [MonedaRepository] Sesión refrescada con éxito. Reintentando operación...")
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