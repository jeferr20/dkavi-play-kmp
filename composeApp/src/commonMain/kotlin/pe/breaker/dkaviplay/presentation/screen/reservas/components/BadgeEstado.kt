package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.presentation.util.toColor

@Composable
fun BadgeEstado(reserva : Reserva) {
    Surface(
        color = reserva.estadoColor.toColor(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = reserva.estado.uppercase(),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}