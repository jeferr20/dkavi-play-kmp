package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.AuthRepository

class RegisterUseCase (private val repository: AuthRepository) {
    suspend operator fun invoke(usuario: String, pass: String) = repository.registerUser(usuario, pass)
}