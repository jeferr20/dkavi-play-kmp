package pe.breaker.dkaviplay.presentation.screen.aceptarReto.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.button.CustomOutlineButtonTextIcon
import pe.breaker.dkaviplay.presentation.theme.colorRedError

@Composable
fun AccionesReto(
    successMessage: String?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClose: () -> Unit
) {
    val isRejected = successMessage?.contains("rechazad", ignoreCase = true) == true ||
            successMessage?.contains("cancelad", ignoreCase = true) == true

    val primaryColor = if (isRejected) colorRedError else Color(0xFF22C55E)

    Box(contentAlignment = Alignment.Center) {
        if (successMessage != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mensaje de Éxito
                Surface(
                    color = primaryColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(
                        1.dp, primaryColor, RoundedCornerShape(16.dp)
                    )
                ) {
                    Text(
                        text = successMessage,
                        modifier = Modifier.padding(20.dp),
                        color = primaryColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón único para ir a ver la reserva activa
                CustomButtonFilled(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = true,
                    onClick = onClose,
                    text = "IR A MIS RESERVAS"
                )
            }
        }
        else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomOutlineButtonTextIcon(
                    modifier = Modifier.weight(1f).height(56.dp),
                    onClick = onReject,
                    colorOutline = colorRedError,
                    enabled = true,
                    text = "PASO"
                )

                CustomButtonFilled(
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = true,
                    onClick = onAccept,
                    text = "¡ACEPTO!"
                )
            }
        }
    }
}