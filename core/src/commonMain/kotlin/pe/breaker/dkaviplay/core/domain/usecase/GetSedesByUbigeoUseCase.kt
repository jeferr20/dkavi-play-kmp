package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.model.Sede
import pe.breaker.dkaviplay.core.domain.repository.SedeRepository

class GetSedesByUbigeoUseCase(private val repository: SedeRepository) {
    suspend operator fun invoke(departamento: String,provincia:String): Result<List<Sede>> {
        return repository.getSedesByDepartamento(departamento,provincia)
    }
}