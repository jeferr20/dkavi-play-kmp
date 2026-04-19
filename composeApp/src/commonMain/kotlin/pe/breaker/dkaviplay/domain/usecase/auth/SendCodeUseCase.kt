package pe.breaker.dkaviplay.domain.usecase.auth

import pe.breaker.dkaviplay.domain.repository.AuthRepository

class SendCodeUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, celular: String) =
        repository.sendCode(email = email, celular = celular)
}