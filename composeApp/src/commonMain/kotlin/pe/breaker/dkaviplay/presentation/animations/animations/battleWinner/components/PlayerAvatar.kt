package pe.breaker.dkaviplay.presentation.animations.animations.battleWinner.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PlayerAvatar(
    imageUrl: String,
    rankRes: DrawableResource,
    name: String,
    isWinner: Boolean,
    screenWidth: androidx.compose.ui.unit.Dp
) {
    val avatarSize = screenWidth * 0.35f
    val colorPrimaryGold = Color(0xFFFFD700)
    val colorGrey = Color(0xFF616161)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Nombre del jugador pulido
        Text(
            text = name.uppercase(),
            color = if (isWinner) colorPrimaryGold else colorGrey,
            fontWeight = if (isWinner) FontWeight.Black else FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Contenedor del Avatar con borde Neón/Glow
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    // Borde Neón para el ganador
                    .then(if (isWinner) {
                        Modifier.border(2.5.dp, colorPrimaryGold, CircleShape)
                            .blur(1.dp) // Sutil difuminado para el efecto glow
                            .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                    } else {
                        Modifier.border(2.dp, colorGrey.copy(alpha = 0.5f), CircleShape)
                    })
                    .background(if (isWinner) Color(0xFF2A2A2A) else Color.DarkGray)
                    .graphicsLayer {
                        // Escala de grises para el perdedor
                        if (!isWinner) {
                            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
                            alpha = 0.7f
                        }
                    },
                contentScale = ContentScale.Crop
            )

            // Rango con sombra proyectada
            Image(
                painter = painterResource(rankRes),
                contentDescription = null,
                modifier = Modifier
                    .size(avatarSize * 0.38f)
                    .offset(x = 5.dp, y = 5.dp) // Pequeño ajuste de posición
                    .graphicsLayer {
                        shadowElevation = 8f
                        shape = CircleShape
                    }
            )
        }
    }
}
