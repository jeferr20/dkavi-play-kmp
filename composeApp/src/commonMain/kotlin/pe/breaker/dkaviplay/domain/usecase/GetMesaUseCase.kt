package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.domain.repository.MesaRepository

class GetMesaUseCase( private val repository: MesaRepository) {
    suspend operator fun invoke(sedeUid: String) = repository.getMesasBySede(sedeUid)
}