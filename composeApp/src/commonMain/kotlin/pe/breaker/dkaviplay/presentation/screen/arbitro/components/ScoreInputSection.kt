package pe.breaker.dkaviplay.presentation.screen.arbitro.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.core.domain.model.TipoJuego
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled

@Composable
fun ScoreInputSection(
    reserva: Reserva,
    resultados: List<Int?>,
    onValueChange: (Int, Int) -> Unit,
    onSend: () -> Unit
) {
    val tipo = remember(reserva.tipoJuego) {
        TipoJuego.fromString(reserva.tipoJuego)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MARCAR GANADOR POR SET",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
        )

        Spacer(modifier = Modifier.height(16.dp))

        repeat(tipo.setsMaximos) { index ->
            val setAnteriorListo = index == 0 || resultados[index - 1] != null

            val victoriasP1 = resultados.count { it == 0 }
            val victoriasP2 = resultados.count { it == 1 }
            val yaHayGanador = tipo.esVictoria(victoriasP1) || tipo.esVictoria(victoriasP2)

            if (setAnteriorListo && (!yaHayGanador || resultados[index] != null)) {
                SetWinnerSelector(
                    setNumber = index + 1,
                    player1Name = reserva.creador,
                    player2Name = reserva.retado,
                    winnerIndex = resultados[index],
                    enabled = !yaHayGanador || resultados[index] != null,
                    onWinnerSelected = { onValueChange(index, it) }
                )
            }
        }

        val victoriasP1 = resultados.count { it == 0 }
        val victoriasP2 = resultados.count { it == 1 }
        val isFinished = tipo.esVictoria(victoriasP1) || tipo.esVictoria(victoriasP2)

        Spacer(modifier = Modifier.height(32.dp))

        CustomButtonFilled(
            enabled = isFinished,
            onClick = onSend,
            text = if (isFinished) "REGISTRAR MARCADOR FINAL" else "PENDIENTE..."
        )
    }
}