package pe.breaker.dkaviplay.presentation.animations.animations.battleLoser.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.animations.AshParticle

@Composable
fun AshEffect(ash: AshParticle, screenHeightDp: Float, onFinished: () -> Unit) {
    // Iniciamos desde el fondo del contenedor (screenHeightDp)
    val animY = remember { Animatable(screenHeightDp + 50f) }
    val animX = remember { Animatable(ash.startX) }
    val rotation = remember { Animatable(ash.rotationStart) }

    LaunchedEffect(Unit) {
        launch {
            animY.animateTo(-100f, tween(ash.duration, easing = LinearEasing))
            onFinished()
        }
        launch {
            // Efecto de vaivén horizontal (Drift)
            animX.animateTo(ash.startX + ash.drift, tween(ash.duration, easing = EaseInOutSine))
        }
        launch {
            rotation.animateTo(rotation.value + 720f, tween(ash.duration))
        }
    }

    Box(
        modifier = Modifier
            .offset(x = animX.value.dp, y = animY.value.dp)
            .rotate(rotation.value)
            .size(ash.size.dp)
            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(1.dp))
    )
}

@Composable
fun WinnerRow(name: String, url: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(horizontalAlignment = Alignment.End) {
            Text("GANADOR", color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text(name, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.size(45.dp).clip(CircleShape).border(1.dp, Color(0xFFFFD700), CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ActionButtons(onShare: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(
            onClick = onShare,
            modifier = Modifier.fillMaxWidth(0.82f).height(54.dp),
            border = BorderStroke(1.2.dp, Color.White.copy(0.4f)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("COMPARTIR", color = Color.White, letterSpacing = 1.sp)
        }
    }
}