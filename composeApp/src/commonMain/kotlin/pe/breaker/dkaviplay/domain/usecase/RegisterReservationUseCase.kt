package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.data.remote.dto.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class RegisterReservationUseCase(private val reservaRepository : ReservaRepository) {
    suspend operator fun invoke(reserva: RegisterReservaRequestDTO) = reservaRepository.registroReserva(reserva)
}