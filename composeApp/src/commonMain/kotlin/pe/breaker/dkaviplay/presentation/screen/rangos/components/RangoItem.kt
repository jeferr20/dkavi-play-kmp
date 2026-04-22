package pe.breaker.dkaviplay.presentation.screen.rangos.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper

@Composable
fun RangoItem(
    modifier: Modifier = Modifier,
    nombreRango: String,
    puntosRango: String,
    isActual: Boolean = false,
    isCompleted: Boolean = false,
    isLocked: Boolean = false
) {
    // Configuramos la apariencia según el estado
    val contentAlpha = if (isLocked) 0.4f else 1f
    val containerColor = when {
        isActual -> colorPrimary.copy(alpha = 0.15f)
        isCompleted -> Color(0xFF1A1A1A).copy(alpha = 0.5f)
        else -> Color(0xFF1A1A1A)
    }

    val borderColor = when {
        isActual -> colorPrimary
        isCompleted -> Color.Green.copy(alpha = 0.3f)
        else -> Color.White.copy(alpha = 0.05f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Para la línea conectora lateral
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // INDICADOR LATERAL (Línea de tiempo)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight().width(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (isActual) 16.dp else 10.dp)
                    .clip(CircleShape)
                    .background(if (isActual || isCompleted) colorPrimary else Color.Gray.copy(alpha = 0.5f))
                    .border(2.dp, Color.Black, CircleShape)
            )
            // Línea que conecta con el siguiente (no se muestra en el último rango)
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            listOf(if (isCompleted) colorPrimary else Color.Gray.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // TARJETA DEL RANGO
        Card(
            modifier = Modifier
                .weight(1f)
                .alpha(contentAlpha),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(if (isActual) 2.dp else 1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // IMAGEN DEL RANGO
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(RankResourceMapper.getDrawableByRank(nombreRango)),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp),
                        // Filtro blanco y negro si está bloqueado
                        colorFilter = if (isLocked) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nombreRango.uppercase(),
                        color = if (isActual) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = if (isActual) FontWeight.Black else FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = puntosRango,
                        color = if (isActual) colorPrimary else Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // ICONO DE ESTADO FINAL (Check, Candado o Flecha)
                when {
                    isCompleted -> Icon(Icons.Default.CheckCircle, "Completado", tint = Color.Green.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                    isLocked -> Icon(Icons.Default.Lock, "Bloqueado", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    isActual -> Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Actual", tint = colorPrimary)
                }
            }
        }
    }
}