package pe.breaker.dkaviplay.presentation.screen.registerReserva.components

import androidx.compose.runtime.Composable
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.registerReserva.RegisterReservationScreenState
import pe.breaker.dkaviplay.presentation.util.StatusUiType

@Composable
fun ReservationStatusOverlay(
    state: RegisterReservationScreenState,
    onClearError: () -> Unit,
    onSuccess: () -> Unit
) {
    if (state.isLoading) LoadingDialog(message = "Registrando Reserva...")

    if (state.isLoadingMesa) LoadingDialog(message = "Consultando Mesas...")

    state.errorMessage?.let {
        StatusDialog(status = StatusUiType.ERROR, message = it, onDismiss = onClearError)
    }

    if (state.isSuccess) {
        StatusDialog(
            status = StatusUiType.SUCCESS,
            message = "Reserva realizada con éxito",
//            message = state.successMessage ?: "Reserva realizada con éxito",
            onDismiss = onSuccess
        )
    }
}