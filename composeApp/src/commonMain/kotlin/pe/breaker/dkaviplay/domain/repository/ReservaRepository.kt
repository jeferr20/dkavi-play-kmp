package pe.breaker.dkaviplay.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.data.remote.reserva.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.ReservaEstado

interface ReservaRepository {
    suspend fun registroReserva(reserva: RegisterReservaRequestDTO): Result<String>
    suspend fun getReservaById(reservaId: String): Result<Reserva>
    fun getReservasFlow(usuarioUid: String?): Flow<List<Reserva>>
    suspend fun responderReto(
        reservaId: String,
        mesa: String,
        sedeUid: String,
        aceptar: Boolean
    ): Result<String>
    suspend fun eliminarReserva(reservaUid: String): Result<String>
    suspend fun updateEstadoReserva(
        reservaEstado: ReservaEstado,
        reservaUid: String
    ): Result<String>
}