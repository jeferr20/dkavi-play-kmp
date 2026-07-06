package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.domain.repository.AuthRepository

class DeleteAccountUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.deleteAccount()
    }
}