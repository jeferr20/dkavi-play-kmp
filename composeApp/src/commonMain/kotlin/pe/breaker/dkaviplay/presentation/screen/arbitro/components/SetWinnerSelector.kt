package pe.breaker.dkaviplay.presentation.screen.arbitro.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SetWinnerSelector(
    setNumber: Int,
    player1Name: String,
    player2Name: String,
    winnerIndex: Int?, // null = no seleccionado, 0 = P1, 1 = P2
    enabled: Boolean = true,
    onWinnerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.4f)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "SET $setNumber",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón Jugador 1
            WinnerChip(
                name = player1Name,
                isSelected = winnerIndex == 0,
                onClick = { if (enabled) onWinnerSelected(0) },
                modifier = Modifier.weight(1f)
            )

            // Botón Jugador 2
            WinnerChip(
                name = player2Name,
                isSelected = winnerIndex == 1,
                onClick = { if (enabled) onWinnerSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}