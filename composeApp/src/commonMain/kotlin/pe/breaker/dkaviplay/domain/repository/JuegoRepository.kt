package pe.breaker.dkaviplay.domain.repository

import pe.breaker.dkaviplay.data.remote.dto.ResultadosPartidasDTO
import pe.breaker.dkaviplay.domain.model.Reserva

interface JuegoRepository {
    suspend fun registrarJuego(
        reserva: Reserva,
        tipoValidacion: String,
        arbitro: String?,
        arbitroUid: String?,
        partidas: List<ResultadosPartidasDTO>
    ): Result<String>
    suspend fun actualizarResultados(reserva: Reserva,partidas: List<ResultadosPartidasDTO>) : Result<String>
    suspend fun cerrarAcuerdoMutuo(reserva: Reserva) : Result<String>
}