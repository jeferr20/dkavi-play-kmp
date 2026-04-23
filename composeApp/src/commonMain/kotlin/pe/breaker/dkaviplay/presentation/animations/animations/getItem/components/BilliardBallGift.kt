package pe.breaker.dkaviplay.presentation.animations.animations.getItem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BilliardBallGift(
    taps: Int,
    opened: Boolean,
    prizeRes: DrawableResource,
    ballRes: DrawableResource,
    shake: Float,
    ballScale: Float,
    prizeScale: Float,
    prizeAlpha: Float,
    glowAlpha: Float
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        // --- 1. BRILLO DEL PREMIO ---
        Box(
            Modifier.fillMaxSize()
                .graphicsLayer {
                    alpha = glowAlpha
                    scaleX = 1.5f
                    scaleY = 1.5f
                }
                .background(
                    Brush.radialGradient(listOf(Color(0xFFFFD700).copy(0.3f), Color.Transparent)),
                    CircleShape
                )
        )

        // --- 2. IMAGEN DEL PREMIO ---
        Image(
            painter = painterResource(prizeRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.7f).graphicsLayer {
                alpha = prizeAlpha
                scaleX = prizeScale
                scaleY = prizeScale
            }
        )

        // --- 3. LA BOLA (CON CORRECCIÓN DE COLOR) ---
        if (!opened) {
            Image(
                painter = painterResource(ballRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize(0.8f)
                    .graphicsLayer {
                        translationX = shake
                        scaleX = ballScale
                        scaleY = ballScale
                        // Usamos alpha para suavizar el parpadeo de impacto
                    },
                // CORRECCIÓN AQUÍ:
                // Usamos SrcAtop para que el color SOLO pinte los píxeles de la bola y no el fondo
                colorFilter = if (taps > 0) {
                    ColorFilter.tint(
                        Color.Red.copy(alpha = 0.3f * (taps / 3f)),
                        BlendMode.SrcAtop
                    )
                } else null,
                contentScale = ContentScale.Fit
            )
        }
    }
}