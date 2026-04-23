package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun ErrorReservasState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hubo un error al consultar tus reservas.",
            color = Color.White,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetry) {
            Text("Reintentar", color = colorPrimary)
        }
    }
}