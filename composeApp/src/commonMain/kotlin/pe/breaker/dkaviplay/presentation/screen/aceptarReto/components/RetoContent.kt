package pe.breaker.dkaviplay.presentation.screen.aceptarReto.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.UserQuick

@Composable
fun RetoContent(
    reserva: Reserva,
    retador: UserQuick,
    successMessage: String?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RetadorHeader(retador)

            Spacer(modifier = Modifier.height(32.dp))

            DetalleReservaCard(reserva)

            Spacer(modifier = Modifier.height(12.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            AccionesReto(
                successMessage = successMessage,
                onAccept = onAccept,
                onReject = onReject,
                onClose = onClose
            )
        }
    }
}