package pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ChallengePlayerCard(
    name: String,
    url: String?,
    rankRes: DrawableResource,
    tag: String,
    modifier: Modifier
) {
    val contrastMatrix = remember {
        val c = 1.4f
        ColorMatrix(
            floatArrayOf(
                c, 0f, 0f, 0f, -0.1f,
                0f, c, 0f, 0f, -0.1f,
                0f, 0f, c, 0f, -0.1f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp)) {
            // Aura de fondo
            Box(
                Modifier.size(165.dp).background(
                    Brush.radialGradient(listOf(Color(0xFF3F51B5).copy(0.25f), Color.Transparent)),
                    CircleShape
                )
            )
            // Avatar con Filtro y Efecto de Respiración
            Box(modifier = Modifier.graphicsLayer {
                colorFilter = ColorFilter.colorMatrix(contrastMatrix)
            }) {
                InternalAvatarWithBreathingBorder(url, rankRes)
            }
            // Etiqueta LOCAL / RETADO
            Box(
                Modifier.align(Alignment.BottomCenter).offset(y = (-10).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (tag == "LOCAL") Color(0xFF3F51B5) else Color(0xFF333333))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(tag, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(name, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
    }
}