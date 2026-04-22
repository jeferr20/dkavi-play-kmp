package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.domain.repository.SedeRepository

class GetSedesByUbigeoUseCase(private val repository: SedeRepository) {
    suspend operator fun invoke(departamento: String,provincia:String): Result<List<Sede>> {
        return repository.getSedesByDepartamento(departamento,provincia)
    }
}