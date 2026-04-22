package pe.breaker.dkaviplay.presentation.screen.mainContainer

//
//class MainContainerModel(
//    private val notificationRepo: NotificationRepository,
//    sessionManager: UserSessionManager
//) : ScreenModel{
//
//    fun syncNotificationToken() {
//        screenModelScope.launch {
//            try {
//                val token = Firebase.messaging.getToken()
//                notificationRepo.saveToken(token)
//            } catch (e: Exception) {
//                println("Error sync token: ${e.message}")
//            }
//        }
//    }
//}