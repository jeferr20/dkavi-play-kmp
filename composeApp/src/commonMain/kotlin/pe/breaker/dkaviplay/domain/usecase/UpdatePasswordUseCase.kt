package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.domain.repository.AuthRepository

class UpdatePasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId: Int, newPassword: String) =
        repository.actualizarPassword(userId = userId, newPassword = newPassword)
}