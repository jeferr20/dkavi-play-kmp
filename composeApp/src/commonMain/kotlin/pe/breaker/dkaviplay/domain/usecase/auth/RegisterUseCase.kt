package pe.breaker.dkaviplay.domain.usecase.auth

import pe.breaker.dkaviplay.domain.repository.AuthRepository

class RegisterUseCase (private val repository: AuthRepository) {
    suspend operator fun invoke(usuario: String, pass: String) = repository.registerUser(usuario, pass)
}