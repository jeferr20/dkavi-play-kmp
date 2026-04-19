package pe.breaker.dkaviplay.domain.repository

import pe.breaker.dkaviplay.domain.model.Torneo

interface TorneoRepository {
    suspend fun getTorneos(): Result<List<Torneo>>
    suspend fun incripcion(torneoUid: String): Result<String>
}