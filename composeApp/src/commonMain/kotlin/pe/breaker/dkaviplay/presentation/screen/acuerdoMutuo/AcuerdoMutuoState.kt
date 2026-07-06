package pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo

import pe.breaker.dkaviplay.core.domain.model.Reserva

data class AcuerdoMutuoState(
    val reserva: Reserva? = null,
    val resultados: List<Int?> = listOf(null, null, null),

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)