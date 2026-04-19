package pe.breaker.dkaviplay.domain.repository

import pe.breaker.dkaviplay.domain.model.Mesa

interface MesaRepository {
    suspend fun getMesasBySede(sedeUid: String) : Result<List<Mesa>>
}