package pe.breaker.dkaviplay.presentation.component.horario

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun ItemFechaHorario(
    fecha: String,
    horaInicio: String,
    horaFin: String,
    estaActivo: Boolean,
    onActivoChange: (Boolean) -> Unit,
    onFullDayClick: (Boolean) -> Unit,
    onHoraInicioChange: (String) -> Unit,
    onHoraFinChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val es24h = horaInicio == "00:00" && horaFin == "23:59"
    val customTextSelectionColors = TextSelectionColors(
        handleColor = colorPrimary, // <--- ESTO cambia el color del "icono" debajo del cursor
        backgroundColor = colorPrimary.copy(alpha = 0.4f) // Color del resaltado al seleccionar texto
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                width = 1.dp,
                color = if (estaActivo) colorPrimary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp) // Padding interno para que nada toque los bordes
            .animateContentSize() // Animación automática al abrir/cerrar los campos
    ) {
        // Fila Superior: Fecha y Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fecha.uppercase(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Switch(
                checked = estaActivo,
                onCheckedChange = onActivoChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorPrimary,
                    checkedTrackColor = colorPrimary.copy(alpha = 0.3f)
                )
            )
        }

        // Si el switch está activo, mostramos los inputs de horario
        if (estaActivo) {
            Spacer(modifier = Modifier.height(8.dp))

            FilterChip(
                selected = es24h,
                onClick = {
                    if (!es24h) {
                        onFullDayClick(true)
                    } else {
                        onFullDayClick(false)
                    }
                },
                label = { Text("Full Day", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = colorPrimary,
                    labelColor = Color.White.copy(alpha = 0.6f)
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = Color.White.copy(alpha = 0.1f),
                    selectedBorderColor = colorPrimary,
                    borderWidth = 1.dp,
                    enabled = true,
                    selected = es24h
                ),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (es24h) 0.5f else 1f), // Opacidad baja si es 24h para indicar que está "auto"
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Campo Hora Inicio
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors){
                    OutlinedTextField(
                        value = horaInicio,
                        onValueChange = onHoraInicioChange,
                        modifier = Modifier.weight(1f),
                        enabled = !es24h,
                        placeholder = { Text("00:00", color = Color.Gray) },
                        label = { Text("Inicio", fontSize = 10.sp) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorPrimary,      // Color cuando el usuario hace clic
                            unfocusedBorderColor = colorPrimary.copy(alpha = 0.5f), // Color cuando está inactivo
                            cursorColor = colorPrimary,             // Color de la rayita al escribir
                            focusedLabelColor = colorPrimary,       // Color del texto "Inicio" al subir
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }

                Text(
                    text = "-",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Campo Hora Fin
                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    OutlinedTextField(
                        value = horaFin,
                        onValueChange = onHoraFinChange,
                        modifier = Modifier.weight(1f),
                        enabled = !es24h,
                        placeholder = { Text("00:00", color = Color.Gray) },
                        label = { Text("Fin", fontSize = 10.sp) },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorPrimary,
                            unfocusedBorderColor = colorPrimary.copy(alpha = 0.5f),
                            cursorColor = colorPrimary,
                            focusedLabelColor = colorPrimary,
                            unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    }
}