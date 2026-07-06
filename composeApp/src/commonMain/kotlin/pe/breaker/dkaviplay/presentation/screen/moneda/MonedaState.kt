package pe.breaker.dkaviplay.presentation.screen.moneda

sealed interface MonedaUiEvent {
    data class OpenWhatsApp(val url: String) : MonedaUiEvent
}

data class MonedaState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionEvent: MonedaUiEvent? = null
)