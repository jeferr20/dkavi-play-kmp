package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.AuthRepository

class VerifyCodeUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(userId:String,code: String) =
        repository.verifyPassword(userId,code)
}