package pe.breaker.dkaviplay.presentation.screen.mainConatiner

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.repository.NotificationRepository

class MainContainerModel(
    private val notificationRepo: NotificationRepository,
    sessionManager: UserSessionManager
) : ScreenModel{

    val globalEvents = sessionManager.globalEvent
    val imagenUser = sessionManager.getCurrentUsuario()?.urlImagen

    fun syncNotificationToken() {
        screenModelScope.launch {
            /*TODO
            try {
                val token = Firebase.messaging.getToken()
                notificationRepo.saveToken(token)
            } catch (e: Exception) {
                println("Error sync token: ${e.message}")
            }
             */
        }
    }
}