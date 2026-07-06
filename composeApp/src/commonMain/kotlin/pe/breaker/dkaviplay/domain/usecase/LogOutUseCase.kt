package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.domain.repository.AuthRepository

class LogOutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() {
        authRepository.logOut()
    }
}