package pe.breaker.dkaviplay.domain.usecase.torneo

import pe.breaker.dkaviplay.domain.model.Torneo
import pe.breaker.dkaviplay.domain.repository.TorneoRepository

class GetTorneosUseCase(private val repository: TorneoRepository) {
    suspend operator fun invoke(): Result<List<Torneo>> { return repository.getTorneos() }
}