package pe.breaker.dkaviplay.domain.usecase.reserva

import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class GetReservationByIdUseCase(private val reservaRepository: ReservaRepository) {
    suspend operator fun invoke(reservaId: String) = reservaRepository.getReservaById(reservaId)
}