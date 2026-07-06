package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.presentation.screen.reservas.ReservasState
import pe.breaker.dkaviplay.presentation.util.formatToRelativeDate

@Composable
fun ReservasContent(
    state: ReservasState,
    onEliminar: (Reserva) -> Unit,
    onAceptarRechazarReto: (Reserva) -> Unit,
    onStartGame: (Reserva) -> Unit,
    onVerificarResultado: (Reserva) -> Unit,
    onVerResulatdos: (Reserva) -> Unit,
    onRetry: () -> Unit,
    onShowInventario: (Reserva) -> Unit,
    onShowQrPago: (Reserva) -> Unit
) {
    if (!state.isLoading && state.reservas.isEmpty()) {
        EmptyReservas(onRetry = onRetry)
        return
    }

    val reservasAgrupadas = remember(state.reservas) {
        state.reservas.groupBy { formatToRelativeDate(it.fechaInicio) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(bottom = 70.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        reservasAgrupadas.forEach { (fechaRelativa, listaDeReservas) ->

            stickyHeader {
                HeaderFecha(texto = fechaRelativa)
            }

            items(
                items = listaDeReservas,
                key = { it.reservaUid }
            ) { reserva ->
                ReservaItem(
                    reserva = reserva,
                    currentUsuario = state.currentUserUid,
                    onEliminar = onEliminar,
                    onAceptarRechazarReto = onAceptarRechazarReto,
                    onStartGameClick = onStartGame,
                    onVerificarResultado = onVerificarResultado,
                    onVerResultados = onVerResulatdos,
                    onShowInventario = onShowInventario,
                    onShowQrPago = onShowQrPago
                )
            }
        }
    }
}