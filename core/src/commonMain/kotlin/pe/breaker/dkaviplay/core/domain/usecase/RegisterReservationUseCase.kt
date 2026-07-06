package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.data.remote.dto.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.core.domain.repository.ReservaRepository

class RegisterReservationUseCase(private val reservaRepository : ReservaRepository) {
    suspend operator fun invoke(reserva: RegisterReservaRequestDTO) = reservaRepository.registroReserva(reserva)
}