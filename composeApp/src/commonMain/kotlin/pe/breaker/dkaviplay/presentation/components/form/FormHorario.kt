package pe.breaker.dkaviplay.presentation.components.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.components.horario.ItemFechaHorario
import pe.breaker.dkaviplay.presentation.components.horario.ToggleAllday
import pe.breaker.dkaviplay.presentation.screen.registerDatos.DiaHorarioState

@Composable
fun FormHorario(
    label: String,
    horarios: List<DiaHorarioState>, // Recibimos la lista de estados desde el ScreenModel
    onHorarioChange: (Int, DiaHorarioState) -> Unit, // Callback para actualizar
    onFullDayClick:(Int, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onAllDayToggle: (Boolean) -> Unit,
    error: String? = null
) {
    val isAllDay = remember(horarios) {
        horarios.all { it.habilitado && it.horaInicio == "00:00" && it.horaFin == "23:59" }
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        ToggleAllday(
            onAllDayToggle = onAllDayToggle,
            isAllDay = isAllDay
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(visible = !isAllDay){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                horarios.forEachIndexed { index, dia ->
                    ItemFechaHorario(
                        modifier = Modifier.width(280.dp),
                        fecha = dia.nombre,
                        estaActivo = dia.habilitado,
                        horaInicio = dia.horaInicio,
                        horaFin = dia.horaFin,
                        onActivoChange = {
                            onHorarioChange(index, dia.copy(habilitado = it))
                        },
                        onHoraInicioChange = {
                            onHorarioChange(index, dia.copy(horaInicio = it))
                        },
                        onHoraFinChange = {
                            onHorarioChange(index, dia.copy(horaFin = it))
                        },
                        onFullDayClick = {
                            onFullDayClick(index, it)
                        }
                    )
                }
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
