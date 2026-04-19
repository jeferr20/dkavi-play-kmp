package pe.breaker.dkaviplay.domain.usecase.quickPlayDetail

import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository

class GetUsuarioQuickPlayUseCase(
    private val repository: QuickPlayRepository
) {
    suspend operator fun invoke(userUid: String): Result<UserQuick> {
        return repository.getUser(userUid)
    }
}