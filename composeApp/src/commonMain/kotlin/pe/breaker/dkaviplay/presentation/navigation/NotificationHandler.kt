package pe.breaker.dkaviplay.presentation.navigation

class NotificationHandler(
    private val navBus: GlobalNavigationBus
) {
    fun handleNotificationAction(data: Map<String, String>) {
        val action = data["action"]
        val id = data["id"]

        when (action) {
            "GO_RESERVATIONS" -> {
                navBus.emit(NavigationEvent.GoToReservations)
            }
            "GO_PROFILE" -> {
                navBus.emit(NavigationEvent.GoToProfile)
            }
            "GO_ACEPTAR_RETO" -> {
                navBus.emit(NavigationEvent.GoToAceptarReto(id ?: ""))
            }
            "GO_VERIFICAR_RESULTADOS" -> {
                navBus.emit(NavigationEvent.GoToVerificarResultados(id ?: ""))
            }
            "GO_VER_RESULTADOS" -> {
                navBus.emit(NavigationEvent.GoToResultadoPartida(id ?: ""))
            }
        }
    }
}