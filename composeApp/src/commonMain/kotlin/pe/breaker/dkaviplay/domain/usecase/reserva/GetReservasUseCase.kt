package pe.breaker.dkaviplay.domain.usecase.reserva

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class GetReservasUseCase(private val repository: ReservaRepository) {
    operator fun invoke(usuarioUid: String): Flow<List<Reserva>> {
        return repository.getReservasFlow(usuarioUid)
    }
}