package pe.breaker.dkaviplay.presentation.animations.animations.getItem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.domain.animations.ParticleState
import pe.breaker.dkaviplay.presentation.animations.animations.getItem.components.BilliardBackground
import pe.breaker.dkaviplay.presentation.animations.animations.getItem.components.BilliardBallGift
import pe.breaker.dkaviplay.presentation.animations.animations.getItem.components.GiftActionButtons
import pe.breaker.dkaviplay.presentation.animations.animations.getItem.components.HitIndicator
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun InteractiveBilliardGiftAnimation(
    prizeName: String,
    prizeRes: DrawableResource,
    ballRes: DrawableResource,
    onShare: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val audioFactory: AudioFactory = koinInject()
    val scope = rememberCoroutineScope()

    // --- ESTADOS DE CONTROL ---
    var taps by remember { mutableStateOf(0) }
    var opened by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    // --- ANIMACIONES ---
    val shake = remember { Animatable(0f) }
    val ballScale = remember { Animatable(1f) }
    val prizeScale = remember { Animatable(0f) }
    val prizeAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }
    val explosionProgress = remember { Animatable(0f) }
    val particles = remember { mutableStateListOf<ParticleState>() }

    val infiniteTransition = rememberInfiniteTransition(label = "IdlePulse")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse)
    )

    // --- CICLO DE VIDA (AUDIO) ---
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) audioFactory.stopBattleMusic()
            if (event == Lifecycle.Event.ON_RESUME) audioFactory.playBattleMusic()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            audioFactory.stopBattleMusic()
        }
    }

    // --- LÓGICA DE INTERACCIÓN ---
    val onTap = {
        if (taps < 3 && !opened) {
            taps++
            scope.launch {
                val intensity = 12f * taps
                shake.animateTo(intensity, tween(50))
                shake.animateTo(-intensity, tween(50))
                shake.animateTo(0f, spring(Spring.DampingRatioHighBouncy))

                if (taps == 3) {
                    opened = true
                    // Explosión Física
                    repeat(100) {
                        particles.add(
                            ParticleState(
                                angle = Random.nextFloat() * 360f,
                                velocity = Random.nextFloat() * 800f + 200f,
                                size = Random.nextFloat() * 10f + 5f,
                                color = if (Random.nextBoolean()) Color(0xFFFFD700) else Color.White
                            )
                        )
                    }
                    launch { ballScale.animateTo(0f, tween(100, easing = EaseInExpo)) }
                    launch { explosionProgress.animateTo(1f, tween(1000, easing = EaseOutQuart)) }
                    launch {
                        delay(150)
                        glowAlpha.animateTo(1f, tween(200))
                        prizeAlpha.animateTo(1f, tween(400))
                        prizeScale.animateTo(1.4f, spring(Spring.DampingRatioMediumBouncy))
                        prizeScale.animateTo(1f, spring())
                        showButtons = true
                    }
                }
            }
        }
    }

    // --- UI ESTRUCTURAL ---
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = null, indication = null) { onTap() },
        contentAlignment = Alignment.Center
    ) {
        BilliardBackground(auraScale)
        if (opened) {
            Canvas(Modifier.fillMaxSize()) {
                val prog = explosionProgress.value
                particles.forEach { p ->
                    val rad = (p.angle * PI / 180).toFloat()
                    val dist = p.velocity * prog
                    val x = center.x + cos(rad) * dist
                    val y = center.y + sin(rad) * dist
                    drawCircle(
                        color = p.color,
                        radius = p.size * (1f - prog),
                        center = Offset(x, y),
                        alpha = 1f - prog
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HitIndicator(taps = taps, visible = !opened)

            Spacer(Modifier.height(this@BoxWithConstraints.maxHeight * 0.05f))

            BilliardBallGift(
                taps = taps,
                opened = opened,
                prizeRes = prizeRes,
                ballRes = ballRes,
                shake = shake.value,
                ballScale = ballScale.value,
                prizeScale = prizeScale.value,
                prizeAlpha = prizeAlpha.value,
                glowAlpha = glowAlpha.value
            )

            Spacer(Modifier.height(32.dp))

            GiftActionButtons(
                visible = showButtons,
                prizeName = prizeName,
                onShare = onShare,
                onContinue = onContinue
            )
        }
    }
}