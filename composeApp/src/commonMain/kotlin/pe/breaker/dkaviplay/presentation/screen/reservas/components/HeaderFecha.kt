package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun HeaderFecha(texto: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black) // Fondo sólido para que no se traslapen las cards al hacer scroll
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = texto,
            color = colorPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )
    }
}