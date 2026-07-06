package pe.breaker.dkaviplay.core.domain.usecase

import pe.breaker.dkaviplay.core.domain.repository.NotificationRepository

class SendNotificacionUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(
        user: String,
        title: String,
        message: String,
        accion: String
    ): Result<String> {
        return repository.sendNotification(user, title, message, accion)
    }
}