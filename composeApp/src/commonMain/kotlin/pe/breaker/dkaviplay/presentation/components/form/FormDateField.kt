package pe.breaker.dkaviplay.presentation.components.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.components.EditText
import pe.breaker.dkaviplay.presentation.components.datetime.DatePickerModal
import pe.breaker.dkaviplay.presentation.util.InputType

@Composable
fun FormDateField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onDateSelected: (Long?) -> Unit,
    placeholder: String = "Seleccionar fecha",
    icon: ImageVector? = Icons.Default.CalendarToday,
    error: String? = null,
    restringirFechaActual: Boolean
) {
    var showModal by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Box {
            EditText(
                value = value,
                onValueChange = {}, // No se escribe manualmente
                placeholder = placeholder,
                icon = icon,
                inputType = InputType.TEXTO,
                error = error,
                modifier = Modifier.fillMaxWidth()
            )

            // Capa invisible para capturar el click y bloquear el teclado
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showModal = true }
            )
        }
    }

    if (showModal) {
        DatePickerModal(
            restringirFechaActual = restringirFechaActual,
            onDateSelected = { millis ->
                onDateSelected(millis)
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}