package pe.breaker.dkaviplay.presentation.screen.registerReserva.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun ReservationTimeSection(
    title: String,
    fechaText: String,
    horaText: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ItemFormReservation(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CalendarToday,
                text = fechaText,
                onClick = onDateClick,
                colorBackground = colorBlackSurface,
                colorIcon = colorPrimary,
                colorText = Color.White
            )
            ItemFormReservation(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                text = horaText,
                onClick = onTimeClick,
                colorBackground = colorBlackSurface,
                colorIcon = colorPrimary,
                colorText = Color.White
            )
        }
    }
}