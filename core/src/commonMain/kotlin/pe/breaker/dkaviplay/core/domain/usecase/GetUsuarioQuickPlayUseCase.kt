package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.model.UserQuick
import pe.breaker.dkaviplay.core.domain.repository.QuickPlayRepository

class GetUsuarioQuickPlayUseCase(
    private val repository: QuickPlayRepository
) {
    suspend operator fun invoke(userUid: String): Result<UserQuick> {
        return repository.getUser(userUid)
    }
}