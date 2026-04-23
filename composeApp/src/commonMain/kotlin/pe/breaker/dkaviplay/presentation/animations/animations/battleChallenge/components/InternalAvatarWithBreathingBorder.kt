package pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun InternalAvatarWithBreathingBorder(imageUrl: String?, rankRes: DrawableResource) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    // Opacidad sutil
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Tamaño del resplandor exterior
    val glowSize by infiniteTransition.animateValue(
        initialValue = 140.dp,
        targetValue = 155.dp, // Crece hacia afuera
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
        // --- ESTE ES EL BORDE QUE RESPIRA HACIA AFUERA ---
        Box(
            Modifier
                .size(glowSize)
                .border(2.dp, Color.White.copy(alpha = borderAlpha), CircleShape)
        )

        // --- EL AVATAR FIJO ---
        Box(contentAlignment = Alignment.BottomEnd) {
            if (!imageUrl.isNullOrBlank()){
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                    contentScale = ContentScale.Crop
                )
            }else{
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(3.dp, colorPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(80.dp))
                }
            }

            // Rango/Rank
            Image(
                painter = painterResource(rankRes),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .offset(x = 4.dp, y = 4.dp)
            )
        }
    }
}