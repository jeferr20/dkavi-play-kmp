package pe.breaker.dkaviplay.presentation.animations.animations.battleLoser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import pe.breaker.dkaviplay.domain.animations.AshParticle
import pe.breaker.dkaviplay.presentation.animations.animations.battleLoser.components.AshEffect
import pe.breaker.dkaviplay.presentation.animations.animations.battleLoser.components.DefeatAvatar
import pe.breaker.dkaviplay.presentation.animations.animations.battleLoser.components.WinnerRow

@Composable
fun BattleLoserAnimation(
    winnerName: String,
    loserName: String,
    winnerProfileUrl: String,
    loserProfileUrl: String,
    loserRankRes: DrawableResource,
    onShare: () -> Unit = {},
) {
    val loserScale = remember { Animatable(1.1f) }
    val glitchOffset = remember { Animatable(0f) }
    val grayscaleFactor = remember { Animatable(0f) }
    val uiAlpha = remember { Animatable(0f) }

    val ashParticles = remember { mutableStateListOf<AshParticle>() }

    // Usamos BoxWithConstraints para obtener el tamaño exacto del espacio disponible
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A))
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Convertimos a DP para el manejo de offset de partículas si es necesario
        val screenWidthDp = maxWidth.value
        val screenHeightDp = maxHeight.value

        // 🎯 GENERACIÓN DE CENIZAS CONSTANTE (Basada en el ancho de BoxWithConstraints)
        LaunchedEffect(Unit) {
            var count = 0
            while (true) {
                if (ashParticles.size < 45) {
                    ashParticles.add(AshParticle.generate(count++, screenWidthDp))
                }
                delay(120) // Un poco más rápido para mayor fluidez
            }
        }

        // 🎯 SECUENCIA DE ANIMACIÓN (Se mantiene igual, es la lógica core)
        LaunchedEffect(Unit) {
            launch {
                repeat(12) {
                    glitchOffset.animateTo(if (it % 2 == 0) 10f else -10f, tween(35))
                }
                glitchOffset.animateTo(0f)
            }
            launch {
                loserScale.animateTo(0.85f, tween(1800, easing = EaseOutBack))
            }
            launch {
                delay(500)
                grayscaleFactor.animateTo(1f, tween(2200))
            }
            launch {
                delay(1400)
                uiAlpha.animateTo(1f, tween(1200))
            }
        }

        // Fondo con Gradiente Radial (Viñeta)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color(0xFF310000).copy(alpha = 0.45f)),
                        radius = widthPx * 0.8f // Proporcional al ancho real
                    )
                )
        )

        // 🔥 EFECTO DE CENIZAS (Usando la altura de BoxWithConstraints)
        ashParticles.forEach { ash ->
            AshEffect(ash, screenHeightDp) {
                ashParticles.remove(ash)
            }
        }

        // --- UI CONTENT ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(0.15f))

            Text(
                "DERROTA",
                color = Color.White.copy(alpha = 0.9f),
                style = TextStyle(
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 12.sp,
                    shadow = Shadow(Color.Red.copy(0.6f), blurRadius = 35f)
                )
            )

            Spacer(modifier = Modifier.height(45.dp))

            // Avatar con Modificadores de GraphicsLayer
            Box(
                modifier = Modifier.graphicsLayer {
                    translationX = glitchOffset.value
                    scaleX = loserScale.value
                    scaleY = loserScale.value
                }
            ) {
                DefeatAvatar(
                    url = loserProfileUrl,
                    rankRes = loserRankRes,
                    saturation = 1f - grayscaleFactor.value
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                loserName.uppercase(),
                color = Color.Gray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.weight(0.1f))

            // Sección de Ganador y Botones
            AnimatedVisibility(
                visible = uiAlpha.value > 0.95f,
                enter = fadeIn() + slideInVertically { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    WinnerRow(winnerName, winnerProfileUrl)

                    Spacer(modifier = Modifier.height(40.dp))

//                    ActionButtons(onShare)
                }
            }
            Spacer(modifier = Modifier.weight(0.15f))
        }
    }
}