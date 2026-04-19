package pe.breaker.dkaviplay.domain.usecase.auth

import pe.breaker.dkaviplay.data.remote.RegisterUsuarioRequestDto
import pe.breaker.dkaviplay.domain.repository.AuthRepository

class RegisterPersonaUseCase (private val repository: AuthRepository) {
    suspend operator fun invoke(persona: RegisterUsuarioRequestDto) = repository.registerPersona(persona)
}