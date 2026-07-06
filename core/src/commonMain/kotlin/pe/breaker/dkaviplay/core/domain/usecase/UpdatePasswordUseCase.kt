package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.AuthRepository

class UpdatePasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: String, newPassword: String) =
        repository.actualizarPassword(userId = userId, newPassword = newPassword)
}