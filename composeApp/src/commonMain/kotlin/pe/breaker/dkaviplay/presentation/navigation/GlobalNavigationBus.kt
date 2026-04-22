package pe.breaker.dkaviplay.presentation.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object GlobalNavigationBus {
    private val _currentTabTarget = MutableStateFlow<NavigationEvent?>(null)
    val currentTabTarget = _currentTabTarget.asStateFlow()

    fun emit(event: NavigationEvent) {
        _currentTabTarget.value = event
    }

    fun clear() {
        _currentTabTarget.value = null
    }
}

sealed class NavigationEvent {
    data object GoToReservations : NavigationEvent()
    data object GoToProfile : NavigationEvent()
    data class GoToAceptarReto(val reservaId: String) : NavigationEvent()
    data class GoToVerificarResultados(val reservaId: String) : NavigationEvent()
    data class GoToResultadoPartida(val reservaId: String) : NavigationEvent()
}