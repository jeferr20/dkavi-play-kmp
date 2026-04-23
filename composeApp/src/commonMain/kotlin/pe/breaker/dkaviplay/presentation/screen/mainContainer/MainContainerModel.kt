package pe.breaker.dkaviplay.presentation.screen.mainContainer

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.repository.NotificationRepository

class MainContainerModel(
    private val notificationRepo: NotificationRepository,
    sessionManager: UserSessionManager
) : ScreenModel {

    fun syncNotificationToken() {
        screenModelScope.launch {
            try {
//                val token = Firebase.messaging.getToken()
//                notificationRepo.saveToken(token)
            } catch (e: Exception) {
                println("Error sync token: ${e.message}")
            }
        }
    }
}