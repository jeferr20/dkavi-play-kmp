package pe.breaker.dkaviplay.domain.usecase.perfil

import pe.breaker.dkaviplay.domain.repository.AuthRepository

class UploadProfileImageUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(byteArray: ByteArray): Result<String> {
        return repository.uploadProfileImage(byteArray)
    }
}