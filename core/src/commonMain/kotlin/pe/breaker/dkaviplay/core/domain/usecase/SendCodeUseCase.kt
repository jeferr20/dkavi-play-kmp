package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.AuthRepository

class SendCodeUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, celular: String) =
        repository.sendCode(email = email, celular = celular)
}