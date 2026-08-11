package pe.breaker.dkaviplay.domain.usecase.reserva

import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class RefreshReservaUseCase(private val repository: ReservaRepository) {
    suspend operator fun invoke(uid: String) {
        repository.refreshReservas(uid)
    }
}