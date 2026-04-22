package pe.breaker.dkaviplay.presentation.components.datetime

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(
            containerColor = colorBlackSurface,
        ),
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) {
                Text("Aceptar", color = colorPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White.copy(alpha = 0.6f))
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    // Reloj (Esfera)
                    clockDialColor = Color.White.copy(alpha = 0.05f),
                    clockDialSelectedContentColor = Color.Black,
                    clockDialUnselectedContentColor = Color.White,

                    // Selector (La aguja/punto)
                    selectorColor = colorPrimary,
                    containerColor = colorBlackSurface,

                    // Bordes y Selectores de Periodo (AM/PM)
                    periodSelectorBorderColor = colorPrimary,
                    periodSelectorSelectedContainerColor = colorPrimary,
                    periodSelectorUnselectedContainerColor = Color.Transparent,
                    periodSelectorSelectedContentColor = Color.Black,
                    periodSelectorUnselectedContentColor = Color.White,

                    // Selectores de tiempo (Los cuadros de HH:MM)
                    timeSelectorSelectedContainerColor = colorPrimary.copy(alpha = 0.2f),
                    timeSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.05f),
                    timeSelectorSelectedContentColor = colorPrimary,
                    timeSelectorUnselectedContentColor = Color.White
                )
            )
        }
    }
}