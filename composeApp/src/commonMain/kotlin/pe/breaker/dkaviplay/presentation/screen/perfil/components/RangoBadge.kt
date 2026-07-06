package pe.breaker.dkaviplay.presentation.screen.perfil.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun RangoBadge(
    modifier: Modifier = Modifier,
    rango: String
) {
    Surface(
        modifier = modifier,
        color = Color(0xFF161616),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colorPrimary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rango.uppercase(),
                color = colorPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}