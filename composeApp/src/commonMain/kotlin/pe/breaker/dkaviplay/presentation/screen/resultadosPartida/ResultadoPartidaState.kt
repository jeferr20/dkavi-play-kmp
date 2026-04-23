package pe.breaker.dkaviplay.presentation.screen.resultadosPartida

import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.UserQuick

data class ResultadoPartidaState(
    val currentUserId: String? = null,
    val reserva: Reserva? = null,
    val ganador: UserQuick? = null,
    val perdedor: UserQuick? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)