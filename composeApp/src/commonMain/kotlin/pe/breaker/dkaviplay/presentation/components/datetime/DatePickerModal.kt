package pe.breaker.dkaviplay.presentation.components.datetime

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import kotlin.time.Clock

@Composable
fun DatePickerModal(
    restringirFechaActual: Boolean = true,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val todayStartMillis = remember {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val startOfDay = LocalDateTime(
            localDateTime.year,
            localDateTime.month,
            localDateTime.day,
            0, 0, 0, 0
        )
        startOfDay.toInstant(TimeZone.UTC).toEpochMilliseconds()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = todayStartMillis,
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return if (restringirFechaActual) {
                    utcTimeMillis >= todayStartMillis
                } else {
                    true
                }
            }

            override fun isSelectableYear(year: Int): Boolean {
                return if (restringirFechaActual) {
                    year >= Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
                } else {
                    true
                }
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(
            containerColor = colorBlackSurface,
        ),
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
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
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                containerColor = colorBlackSurface,
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                dayContentColor = Color.White,
                weekdayContentColor = Color.White.copy(alpha = 0.6f),
                subheadContentColor = Color.White.copy(alpha = 0.6f),
                selectedDayContainerColor = colorPrimary,
                selectedDayContentColor = Color.Black,
                todayContentColor = colorPrimary,
                todayDateBorderColor = colorPrimary,
                yearContentColor = Color.White,
                selectedYearContainerColor = colorPrimary,
                selectedYearContentColor = Color.Black,
                navigationContentColor = Color.White
            )
        )
    }
}
