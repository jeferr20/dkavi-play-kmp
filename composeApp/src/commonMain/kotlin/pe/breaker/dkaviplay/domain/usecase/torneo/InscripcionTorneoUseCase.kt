package pe.breaker.dkaviplay.domain.usecase.torneo

import pe.breaker.dkaviplay.domain.repository.TorneoRepository

class InscripcionTorneoUseCase(private val repository: TorneoRepository) {
    suspend operator fun invoke(torneoUid: String): Result<String> {
        return repository.incripcion(torneoUid)
    }
}