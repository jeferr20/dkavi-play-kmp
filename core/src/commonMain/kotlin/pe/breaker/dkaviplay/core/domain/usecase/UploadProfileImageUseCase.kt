package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.AuthRepository

class UploadProfileImageUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(byteArray: ByteArray): Result<String> {
        return repository.uploadProfileImage(byteArray)
    }
}