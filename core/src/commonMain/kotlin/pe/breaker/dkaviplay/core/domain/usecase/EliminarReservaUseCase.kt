package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.ReservaRepository

class EliminarReservaUseCase(private val respository: ReservaRepository) {
    suspend operator fun invoke(reservaUid: String): Result<String>{ return respository.eliminarReserva(reservaUid)}
}