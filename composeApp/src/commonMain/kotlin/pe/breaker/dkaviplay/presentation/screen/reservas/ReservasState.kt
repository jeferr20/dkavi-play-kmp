package pe.breaker.dkaviplay.presentation.screen.reservas

import pe.breaker.dkaviplay.core.domain.model.Reserva

data class ReservasState (
    val reservas : List<Reserva> = emptyList(),
    val currentUserUid: String = "",
    val currentFilter: ReservaFilter = ReservaFilter.ACTUALES,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)