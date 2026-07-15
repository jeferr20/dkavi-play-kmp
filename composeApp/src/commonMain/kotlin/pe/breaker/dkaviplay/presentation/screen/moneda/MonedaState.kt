package pe.breaker.dkaviplay.presentation.screen.moneda

sealed interface MonedaUiEvent {
    data class OpenWhatsApp(val url: String) : MonedaUiEvent
}

data class MonedaState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isTiendaDisponible: Boolean = false,
    val precioMoneda: Double = 0.0,
    val numPago: String? = null,
    val actionEvent: MonedaUiEvent? = null,

    val showPagoDialog: Boolean = false,
    val montoCalculado: Double = 0.0,
    val cantidadSeleccionada: Int = 0,
    val idempotencyKey: String = ""
)