package pe.breaker.dkaviplay.presentation.animations.animations.battleWinner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutExpo
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.animations.animations.battleWinner.components.AnimatedPreButton
import pe.breaker.dkaviplay.presentation.animations.animations.battleWinner.components.ConfettiCanvas
import pe.breaker.dkaviplay.presentation.animations.animations.battleWinner.components.PlayerAvatar
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory

@Composable
fun BattleWinnerAnimation(
    modifier: Modifier,
    winnerName: String,
    loserName: String,
    winnerProfileUrl: String,
    loserProfileUrl: String,
    winnerRankRes: DrawableResource,
    loserRankRes: DrawableResource,
    trophyRes: DrawableResource,
    onShare: () -> Unit = {}
) {
    val audioFactory: AudioFactory = koinInject()
    val haptic = LocalHapticFeedback.current

    val shockwaveScale = remember { Animatable(0f) }
    val shockwaveAlpha = remember { Animatable(0f) }
    val winnerOffsetY = remember { Animatable(-600f) }
    val winnerScale = remember { Animatable(1f) }
    val loserAlpha = remember { Animatable(1f) }
    var showResults by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    var isMuted by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isMuted) {
        if (isMuted) {
            audioFactory.stopBattleMusic()
        } else {
            audioFactory.playBattleMusic()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                audioFactory.stopBattleMusic()
            } else if (event == Lifecycle.Event.ON_RESUME && !isMuted) {
                audioFactory.playBattleMusic()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            audioFactory.stopBattleMusic()
        }
    }

    LaunchedEffect(Unit) {
        audioFactory.playBattleMusic()
        delay(500)

        // 1. Caída libre acelerada
        winnerOffsetY.animateTo(0f, tween(450, easing = EaseInExpo))

        // 2. --- MOMENTO DEL IMPACTO ---
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)

        launch {
            // Onda de choque expansiva
            shockwaveAlpha.snapTo(0.6f)
            shockwaveScale.animateTo(4f, tween(600, easing = EaseOutExpo))
            shockwaveAlpha.animateTo(0f, tween(600))
        }

        launch {
            loserAlpha.animateTo(0f, tween(150))
            showResults = true
        }

        // 3. Rebote con "Squash and Stretch"
        launch {
            winnerScale.animateTo(1.4f, tween(100)) // Se aplasta
            winnerScale.animateTo(1.15f, spring(Spring.DampingRatioHighBouncy)) // Rebota
        }
    }


    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A0A0A), Color.Black)))
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        ConfettiCanvas(show = showResults)

        IconButton(
            onClick = { isMuted = !isMuted },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 12.dp, end = 16.dp)
                .zIndex(10f)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
        ) {
            Icon(
                imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                contentDescription = if (isMuted) "Activar sonido" else "Silenciar sonido",
                tint = Color.White
            )
        }

        // --- COLUMNA PRINCIPAL DINÁMICA ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween // Distribuye el espacio sobrante
        ) {

            // 1. TROFEO (Arriba)
            AnimatedVisibility(
                visible = showResults,
                enter = scaleIn(
                    initialScale = 0.3f,
                    animationSpec = tween(600, easing = EaseOutBack)
                ) + fadeIn(),
                modifier = Modifier
                    .weight(1f) // Ahora el peso lo tiene la visibilidad directamente
                    .fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    // Resplandor dorado
                    Box(
                        modifier = Modifier
                            .size(screenWidth * 0.3f)
                            .background(Color(0xFFFFD700).copy(0.15f), CircleShape)
                            .blur(30.dp)
                    )
                    // Imagen del Trofeo
                    Image(
                        painter = painterResource(trophyRes),
                        contentDescription = null,
                        modifier = Modifier.size(screenWidth * 0.4f)
                    )
                }
            }

            // 2. EL COMBATE (Centro)
            Box(modifier = Modifier.weight(1.2f).fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {

                // ONDA DE CHOQUE (Shockwave)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = shockwaveScale.value
                            scaleY = shockwaveScale.value
                            alpha = shockwaveAlpha.value
                        }
                        .background(
                            Brush.radialGradient(listOf(Color.White, Color(0xFFFFD700), Color.Transparent)),
                            CircleShape
                        )
                )

                // AVATARES
                if (loserAlpha.value > 0.01f) {
                    Box(modifier = Modifier.graphicsLayer { alpha = loserAlpha.value }) {
                        PlayerAvatar(loserProfileUrl, loserRankRes, loserName, false, screenWidth)
                    }
                }

                Box(
                    modifier = Modifier
                        .offset(y = winnerOffsetY.value.dp)
                        .graphicsLayer {
                            scaleX = winnerScale.value
                            scaleY = winnerScale.value
                            // Pequeña sombra debajo del ganador mientras cae
                            shadowElevation = if (!showResults) 20f else 0f
                        }
                        .zIndex(1f)
                ) {
                    PlayerAvatar(winnerProfileUrl, winnerRankRes, winnerName, true, screenWidth)
                }
            }

            // 3. BOTONES Y TEXTO (Abajo)
            AnimatedVisibility(
                visible = showResults,
                enter = slideInVertically { 100 } + fadeIn(),
                modifier = Modifier.weight(1f)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Centra los botones en su espacio
                ) {
                    Text(
                        text = "¡VICTORIA!",
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFD700),
                                    Color(0xFFFFA500)
                                )
                            ),
                            fontSize = (screenWidth.value * 0.1f).sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedPreButton(delay = 100) {
                        OutlinedButton(
                            onClick = onShare,
                            modifier = Modifier.fillMaxWidth(0.8f).height(54.dp),
                            border = BorderStroke(2.dp, Color.White.copy(0.8f)),
                            shape = RoundedCornerShape(27.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("COMPARTIR RESULTADO", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}