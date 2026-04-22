package pe.breaker.dkaviplay.presentation.screen.aceptarReto

import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.UserQuick

data class AceptarRetoState (
    val reserva: Reserva? = null,
    val retador: UserQuick? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)