package pe.breaker.dkaviplay.domain.usecase.reserva

import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class ResponserRetoUseCase(private val reservaRepository: ReservaRepository) {
    suspend operator fun invoke(reserva: Reserva, aceptar: Boolean) =
        reservaRepository.responderReto(reserva, aceptar)
}