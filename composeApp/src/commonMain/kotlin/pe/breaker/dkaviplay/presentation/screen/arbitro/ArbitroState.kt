package pe.breaker.dkaviplay.presentation.screen.arbitro

import pe.breaker.dkaviplay.domain.model.Reserva

data class ArbitroState(
    val step: ArbitroStep = ArbitroStep.SCANNING,
    val reserva: Reserva? = null,
    val resultados: List<Int?> =listOf(null, null, null),

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorEscaneoMessage: String? = null,
    val errorResultadosMessage: String? = null
)

enum class ArbitroStep {
    SCANNING,    // Escaneando el QR
    REGISTERING  // Ingresando los scores
}