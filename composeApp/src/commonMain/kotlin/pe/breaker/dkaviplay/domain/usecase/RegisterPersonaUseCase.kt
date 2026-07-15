package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.data.remote.dto.request.RequestRegisterInfoUsuarioDTO
import pe.breaker.dkaviplay.domain.repository.AuthRepository

class RegisterPersonaUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(persona: RequestRegisterInfoUsuarioDTO, isLogged: Boolean) =
        repository.registerPersona(persona, isLogged)
}