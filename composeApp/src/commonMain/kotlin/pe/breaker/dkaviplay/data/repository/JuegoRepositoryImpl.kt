package pe.breaker.dkaviplay.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.breaker.dkaviplay.data.remote.dto.ActualizarPuntosRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.CerrarJuegoMutuoRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.RegistrarJuegoRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.ResultadosPartidasDTO
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.repository.JuegoRepository

class JuegoRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionManager: UserSessionManager
) : JuegoRepository {
    override suspend fun registrarJuego(
        reserva: Reserva,
        tipoValidacion: String,
        arbitro: String?,
        arbitroUid: String?,
        partidas: List<ResultadosPartidasDTO>
    ): Result<String> {
        val contrincanteUid = if (sessionManager.getUserUid()  == reserva.creadorUid) {
            reserva.retadoUid
        } else {
            reserva.creadorUid
        }

        val requestBody = RegistrarJuegoRequestDTO(
            uuidUser1 = reserva.creadorUid,
            uuidUser2 = reserva.retadoUid,
            user1 = reserva.creador,
            user2 = reserva.retado,
            tipoJuego = reserva.tipoJuego,
            reservaId = reserva.reservaUid,
            tipoValidacion = tipoValidacion,
            partidas = partidas,
            uuidArbitro = arbitroUid,
            arbitro = arbitro,
            contrincanteUid = contrincanteUid,
            sender = sessionManager.getCurrentUsuario()?.usuario
        )
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/juego/registerJuego") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun actualizarResultados(
        reserva: Reserva,
        partidas: List<ResultadosPartidasDTO>
    ): Result<String> {
        val contrincanteUid = if (sessionManager.getUserUid() == reserva.creadorUid) {
            reserva.retadoUid
        } else {
            reserva.creadorUid
        }

        val requestBody = ActualizarPuntosRequestDTO(
            juegoId = reserva.juegoUid,
            partidas = partidas,
            tipoJuego = reserva.tipoJuego,
            reservaId = reserva.reservaUid,
            contrincanteUid = contrincanteUid,
            sender = sessionManager.getCurrentUsuario()?.usuario
        )
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/juego/updateJuegoMutuoService") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun cerrarAcuerdoMutuo(reserva: Reserva): Result<String> {
        val requestBody = CerrarJuegoMutuoRequestDTO(
            reservaId = reserva.reservaUid,
            juegoId = reserva.juegoUid
        )
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/juego/confirmJuegoService") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }
}