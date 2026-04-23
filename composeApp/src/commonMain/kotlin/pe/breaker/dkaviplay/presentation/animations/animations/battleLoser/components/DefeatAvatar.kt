package pe.breaker.dkaviplay.presentation.animations.animations.battleLoser.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DefeatAvatar(url: String, rankRes: DrawableResource, saturation: Float) {
    val matrix = remember(saturation) {
        ColorMatrix().apply {
            setToSaturation(saturation)
            // Añadimos un poco de contraste negativo cuando pierde
            val m = FloatArray(20)
            m[0] = 1f; m[6] = 1f; m[12] = 1f; m[18] = 1f
            val contrast = 1f + (1f - saturation) * -0.2f
            // (Lógica simplificada para el ejemplo)
        }
    }

    Box(contentAlignment = Alignment.BottomEnd) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                .graphicsLayer { colorFilter = ColorFilter.colorMatrix(matrix) },
            contentScale = ContentScale.Crop
        )
        // Rango también pierde color
        Image(
            painter = painterResource(rankRes),
            contentDescription = null,
            modifier = Modifier.size(54.dp).graphicsLayer {
                alpha = saturation + 0.3f
                if (saturation < 0.5f) colorFilter = ColorFilter.colorMatrix(matrix)
            }
        )
    }
}