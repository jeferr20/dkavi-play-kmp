package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.MesaRepository

class GetMesaUseCase( private val repository: MesaRepository) {
    suspend operator fun invoke(sedeUid: String) = repository.getMesasBySede(sedeUid)
}