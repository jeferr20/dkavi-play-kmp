package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.domain.model.LoginResult
import pe.breaker.dkaviplay.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(usuario: String, pass: String): Result<LoginResult> {
        return repository.login(usuario, pass)
    }
}