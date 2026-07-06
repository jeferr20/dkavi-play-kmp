package pe.breaker.dkaviplay.core.domain.repository

import pe.breaker.dkaviplay.core.domain.model.Mesa

interface MesaRepository {
    suspend fun getMesasBySede(sedeUid: String) : Result<List<Mesa>>
}