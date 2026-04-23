package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class EliminarReservaUseCase(private val respository: ReservaRepository) {
    suspend operator fun invoke(reservaUid: String): Result<String>{ return respository.eliminarReserva(reservaUid)}
}