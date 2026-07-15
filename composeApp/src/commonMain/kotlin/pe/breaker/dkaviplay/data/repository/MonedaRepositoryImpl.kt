package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLParameter
import pe.breaker.dkaviplay.data.remote.dto.request.LogMonedaDTO
import pe.breaker.dkaviplay.data.remote.dto.response.ResponseMensajeCompraMonedaDTO
import pe.breaker.dkaviplay.data.remote.supabase.view.TarifarioView
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.domain.repository.MonedaRepository

class MonedaRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val httpClient: HttpClient,
) : MonedaRepository {
    override suspend fun getPrecioMonedas(sedeUid: Int): Result<TarifarioView> {
        return try {
            val tarifa = supabaseClient
                .from(schema = "dkavi", table = "v_tarifario")
                .select {
                    filter {
                        eq("sede_id", sedeUid)
                    }
                }.decodeSingleOrNull<TarifarioView>()

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
            val response = supabaseClient.postgrest.rpc(
                function = "get_mensaje_compra",
                parameters = mapOf(
                    "p_usuario_id" to userUid,
                    "p_monedas" to monedas
                )
            ) {
                schema = "public"
            }.decodeAs<ResponseMensajeCompraMonedaDTO>()
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
}