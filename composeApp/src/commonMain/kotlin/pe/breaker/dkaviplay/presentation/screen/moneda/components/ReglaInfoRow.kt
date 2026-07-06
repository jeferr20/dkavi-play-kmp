package pe.breaker.dkaviplay.presentation.screen.moneda.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.theme.goldColor


@Composable
fun ReglaInfoRow(
    texto: String,
    destacarCosto: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Viñeta Dorada Estilizada
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(6.dp)
                .background(if (destacarCosto) goldColor else colorPrimary, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = texto,
            color = if (destacarCosto) Color.White else Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp,
            fontWeight = if (destacarCosto) FontWeight.Medium else FontWeight.Normal,
            lineHeight = 20.sp
        )
    }
}