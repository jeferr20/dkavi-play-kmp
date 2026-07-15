package pe.breaker.dkaviplay.presentation.screen.registerReserva

import pe.breaker.dkaviplay.domain.model.Mesa
import pe.breaker.dkaviplay.domain.model.TipoJuego

data class RegisterReservationScreenState(
    val mesas: List<Mesa> = emptyList(),
    val selectedMesa: Mesa? = null,
    val tipoJuego: TipoJuego = TipoJuego.BILLAR,
    val hInicio: String? = null,
    val fInicio: String? = null,
    val hSalida: String? = null,
    val fSalida: String? = null,
    val usuarioRetadoUid: String? = null,
    val usuarioRetado: String? = null,

    val tarifario: Double? = null,
    val usuario: String? = null,
    val sedeUid: String? = null,

    val isLoading: Boolean = false,
    val isLoadingMesa: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)