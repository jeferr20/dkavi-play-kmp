package pe.breaker.dkaviplay.presentation.screen.inventario.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.ItemResourceMapper

@Composable
fun ItemCard(
    item: DetallePremio,
    onClick: () -> Unit = {}
) {
    val cantidad = item.cantidad
    val estaDisponible = cantidad > 0
    val borderColor = if (estaDisponible) colorPrimary.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.1f)
    val contentAlpha = if (estaDisponible) 1f else 0.3f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp) // Un poco más de aire para la descripción
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (estaDisponible) {
                    Brush.verticalGradient(listOf(Color(0xFF1A237E).copy(alpha = 0.4f), Color(0xFF0D1231)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF000000)))
                }
            )
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(enabled = estaDisponible) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            // IMAGEN DEL ÍTEM (item_1.webp, item_2.webp, etc.)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer { alpha = contentAlpha },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(ItemResourceMapper.getItemImage(item.id)),
                    contentDescription = item.nombre,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = if (!estaDisponible) {
                        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.nombre.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                color = if (estaDisponible) Color.White else Color.Gray,
                maxLines = 2
            )
        }

        if (estaDisponible) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                color = colorPrimary,
                shape = CircleShape,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = "x$cantidad",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
            }
        }

        if (!estaDisponible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Bloqueado",
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}