package pe.breaker.dkaviplay.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.core.domain.repository.ReservaRepository

class GetReservasUseCase( private val repository: ReservaRepository) {
    operator fun invoke(usuarioUid: String?): Flow<List<Reserva>> {
        return repository.getReservasFlow(usuarioUid)
    }
}