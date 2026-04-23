package pe.breaker.dkaviplay.presentation.screen.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.PhoneEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.data.mapper.findTodaySchedule
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.presentation.components.button.ActionIconButton
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.theme.colorGrey400
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun SedeDetailContent(
    sede: Sede,
    ocupacion: String,
    onReservationClick: (Sede) -> Unit,
    onQuickPlayClick: (Sede) -> Unit,
    onCallClick: (String) -> Unit,
    onMessageClick: (String) -> Unit
) {
    val todaySchedule = remember(sede.horario) { sede.horario.findTodaySchedule() }
    val hideReserva = true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Mayor separación entre bloques
    ) {
        // --- Cabecera: Nombres y Botones de Acción ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sede.nombreSede,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sede.nombreEmpresa,
                    color = colorGrey400, // Color secundario para la empresa
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionIconButton(
                    icon = Icons.Default.PhoneEnabled,
                    contentDescription = "Llamar",
                    onClick = { onCallClick(sede.numSede) },
                    colorIcon = Color.White,
                    colorBackground = colorPrimary
                )

                ActionIconButton(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat",
                    onClick = { onMessageClick(sede.numSede) },
                    colorIcon = Color.White,
                    colorBackground = colorPrimary
                )
            }
        }

        // --- Tarjetas de Información ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoCard(
                modifier = Modifier.weight(1f),
                label = "Disponibilidad",
                value = "$ocupacion Mesas"
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                label = "Horario de hoy",
                value = todaySchedule
            )
        }

        // --- Botones de Acción Principal ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if(!hideReserva){
                CustomButtonFilled(
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = true,
                    onClick = { onReservationClick(sede) },
                    text = "Reservar"
                )
            }

            CustomButtonFilled(
                modifier = Modifier.weight(1f).height(56.dp),
                enabled = true,
                onClick = { onQuickPlayClick(sede) },
                text = "Retar"
            )
        }
    }
}