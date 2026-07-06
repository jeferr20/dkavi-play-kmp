package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.data.remote.dto.RegisterUsuarioRequestDto
import pe.breaker.dkaviplay.core.domain.repository.AuthRepository

class RegisterPersonaUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(persona: RegisterUsuarioRequestDto, isLogged: Boolean) =
        repository.registerPersona(persona, isLogged)
}