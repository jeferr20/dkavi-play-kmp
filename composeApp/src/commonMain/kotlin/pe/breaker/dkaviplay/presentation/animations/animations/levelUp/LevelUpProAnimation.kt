package pe.breaker.dkaviplay.presentation.animations.animations.levelUp

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory
import kotlin.random.Random

@Composable
fun LevelUpProAnimation(
    previousLevelName: String,
    newLevelName: String,
    userImageUrl: String,
    oldRankRes: DrawableResource,
    newRankRes: DrawableResource,
    onClose: () -> Unit
) {
    val audioFactory: AudioFactory = koinInject()
    val lifecycleOwner = LocalLifecycleOwner.current

    val transitionState = remember { MutableTransitionState(0) } // 0: Inicio, 1: Impacto, 2: Final
    val dropProgress = remember { Animatable(0f) } // 0f arriba, 1f impacto
    val oldRankScaleY = remember { Animatable(1f) }
    val oldRankAlpha = remember { Animatable(1f) }
    val screenShake = remember { Animatable(0f) }
    val flashAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "Background")
    val raysRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing))
    )

    LaunchedEffect(Unit) {
        audioFactory.playBattleMusic()
        delay(600)

        // 1. EL NUEVO RANGO CAE (Física de aceleración)
        dropProgress.animateTo(1f, tween(400, easing = EaseInBack))

        // 2. MOMENTO DEL IMPACTO (Crunch!)
        launch {
            // El viejo se aplasta y desaparece
            launch { oldRankScaleY.animateTo(0.1f, tween(150)) }
            launch { oldRankAlpha.animateTo(0f, tween(200)) }

            // Sacudida de pantalla pro
            repeat(6) { i ->
                val intensity = if (i % 2 == 0) 15f else -15f
                screenShake.animateTo(intensity, tween(40))
            }
            screenShake.animateTo(0f, spring())
        }

        // 3. EFECTOS VISUALES POST-IMPACTO
        flashAlpha.animateTo(0.7f, tween(50))
        transitionState.targetState = 2 // Activa confeti y botones
        flashAlpha.animateTo(0f, tween(600))
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> audioFactory.stopBattleMusic()
                Lifecycle.Event.ON_RESUME -> if (transitionState.targetState < 2) audioFactory.playBattleMusic()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            audioFactory.stopBattleMusic()
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A)),
        contentAlignment = Alignment.Center
    ) {
        val width = maxWidth
        val height = maxHeight

        // --- CAPA 1: RAYOS DE LUZ (Background) ---
        if (transitionState.targetState >= 1) {
            Canvas(Modifier.fillMaxSize().graphicsLayer { rotationZ = raysRotation }) {
                val rayCount = 16
                val angleStep = 360f / rayCount
                for (i in 0 until rayCount) {
                    val path = Path().apply {
                        moveTo(size.center.x, size.center.y)
                        lineTo(size.center.x - 60f, -size.height)
                        lineTo(size.center.x + 60f, -size.height)
                        close()
                    }
                    rotate(i * angleStep) {
                        drawPath(path, Color(0xFFFFD700).copy(alpha = 0.12f))
                    }
                }
            }
        }

        // --- CAPA 2: CONTENEDOR CENTRAL ADAPTABLE ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().offset(y = screenShake.value.dp)
        ) {
            // Contenedor principal con tamaño fijo basado en el ancho
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(width * 0.8f)
            ) {

                // 1. Aura pulsante (Fondo del avatar)
                if (transitionState.targetState == 2) {
                    AuraComponent(width * 0.65f)
                }

                // 2. Avatar del Jugador (Protagonista - Siempre visible)
                AsyncImage(
                    model = userImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(0.6f) // Tamaño del círculo central
                        .clip(CircleShape)
                        .border(
                            width = 4.dp,
                            brush = Brush.sweepGradient(listOf(Color(0xFFFFD700), Color(0xFFB8860B), Color(0xFFFFD700))),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )

                // --- CONTENEDOR DE RANGOS (Posicionado como Insignia) ---
                // Usamos un Box pequeño alineado a la esquina inferior para que no tape el centro
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.35f) // Tamaño del área del rango
                        .align(Alignment.BottomEnd) // Lo mueve a la esquina inferior derecha
                        .offset(x = (-10).dp, y = (-10).dp), // Un pequeño ajuste hacia adentro
                    contentAlignment = Alignment.Center
                ) {
                    // RANGO VIEJO (Siendo aplastado en la esquina)
                    if (oldRankAlpha.value > 0.01f) {
                        Image(
                            painter = painterResource(oldRankRes),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleY = oldRankScaleY.value
                                    scaleX = 1f + (1f - oldRankScaleY.value) * 1.2f
                                    alpha = oldRankAlpha.value
                                }
                        )
                    }

                    // RANGO NUEVO (Cae directo a la esquina)
                    val yOffset = (-height.value.dp) * (1f - dropProgress.value)
                    Image(
                        painter = painterResource(newRankRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(y = yOffset)
                            .graphicsLayer {
                                rotationZ = (1f - dropProgress.value) * 20f
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TEXTOS ---
            Crossfade(targetState = transitionState.targetState) { state ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (state < 2) {
                        Text(previousLevelName.uppercase(), color = Color.Gray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("¡NUEVO RANGO!", color = Color(0xFFFFD700), fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                        Text(
                            newLevelName.uppercase(),
                            color = Color.White,
                            fontSize = (width.value * 0.12).sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // --- CAPA 3: EFECTOS FINALES ---
        if (transitionState.targetState == 2) {
            PremiumConfetti(modifier = Modifier.fillMaxSize())

            Button(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = height * 0.08f)
                    .fillMaxWidth(0.7f)
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5FB4)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("RECLAMAR RECOMPENSA", fontWeight = FontWeight.Black, color = Color.White)
            }
        }

        // Flash de impacto (Capa superior)
        Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = flashAlpha.value)))
    }
}

@Composable
fun AuraComponent(size: Dp) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse)
    )
    Box(
        Modifier.size(size).graphicsLayer { scaleX = scale; scaleY = scale }
            .background(Brush.radialGradient(listOf(Color(0xFFFFD700).copy(alpha = 0.4f), Color.Transparent)), CircleShape)
    )
}

@Composable
fun PremiumConfetti(modifier: Modifier = Modifier) {
    data class Particula(val x: Float, val y: Float, val color: Color, val speedY: Float, val rotationSpeed: Float, val size: Float)
    val particulas = remember {
        List(70) {
            Particula(
                x = Random.nextFloat(), y = Random.nextFloat() * -1f,
                color = listOf(Color(0xFFFFD700), Color(0xFF2D5FB4), Color.White).random(),
                speedY = Random.nextFloat() * 0.01f + 0.005f,
                rotationSpeed = Random.nextFloat() * 8f,
                size = Random.nextFloat() * 15f + 10f
            )
        }
    }
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3500, easing = LinearEasing))
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particulas.forEach { p ->
            val currentY = (p.y + progress) % 1.2f
            if (currentY in 0f..1f) {
                rotate(p.rotationSpeed * progress * 100f, pivot = Offset(p.x * size.width, currentY * size.height)) {
                    drawRect(
                        color = p.color,
                        topLeft = Offset(p.x * size.width, currentY * size.height),
                        size = Size(p.size, p.size / 1.4f),
                        alpha = if (currentY > 0.8f) 1f - (currentY - 0.8f) * 5f else 1f
                    )
                }
            }
        }
    }
}

//@Composable
//fun LevelUpProAnimation(
//    previousLevelName: String,
//    newLevelName: String,
//    userImageUrl: String,
//    oldRankRes: DrawableResource,
//    newRankRes: DrawableResource
//) {
//    val audio: AudioFactory = koinInject()
//    val scope = rememberCoroutineScope()
//
//    // --- ESTADOS DE PASOS ---
//    var currentStep by remember { mutableStateOf(0) } // 0: Intriga, 1: Flash/Cambio, 2: Final
//
//    // --- ANIMACIONES ---
//    val bgAlpha = remember { Animatable(0f) }
//    val flashAlpha = remember { Animatable(0f) }
//    val shakeOffset = remember { Animatable(0f) }
//
//    // Control de Rango Anterior (Explosión)
//    val oldElementScale = remember { Animatable(1f) }
//    val oldElementAlpha = remember { Animatable(1f) }
//
//    // Control de Rango Nuevo (Aparición)
//    val newElementScale = remember { Animatable(0.4f) }
//    val newElementAlpha = remember { Animatable(0f) }
//
//    val infiniteTransition = rememberInfiniteTransition()
//    val auraRotation by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing))
//    )
//
//    val confettiProgress = remember { Animatable(0f) }
//
//    LaunchedEffect(Unit) {
//        // 1. FASE DE INTRIGA
//        audio.playBattleMusic()
//        bgAlpha.animateTo(0.92f, tween(1000))
//
//        delay(1500)
//        currentStep = 1
//
//        // 2. FASE DE EXPLOSIÓN (TRANSFORMACIÓN)
//        launch {
//            flashAlpha.animateTo(1f, tween(150, easing = EaseInExpo))
//            currentStep = 2
//            flashAlpha.animateTo(0f, tween(700, easing = EaseOutQuad))
//        }
//
//        // Animación de salida (Old) y entrada (New)
//        launch {
//            // El rango viejo explota hacia afuera
//            launch { oldElementScale.animateTo(5f, tween(400, easing = EaseOutExpo)) }
//            launch { oldElementAlpha.animateTo(0f, tween(300)) }
//
//            // El rango nuevo entra con rebote después de un micro delay
//            delay(100)
//            launch { newElementAlpha.animateTo(1f, tween(200)) }
//            launch {
//                newElementScale.animateTo(
//                    1.3f,
//                    spring(Spring.DampingRatioHighBouncy, Spring.StiffnessLow)
//                )
//                newElementScale.animateTo(1.1f, spring())
//            }
//        }
//
//        // 3. SHAKE DE IMPACTO
//        launch {
//            repeat(8) {
//                shakeOffset.animateTo(25f, tween(30))
//                shakeOffset.animateTo(-25f, tween(30))
//            }
//            shakeOffset.animateTo(0f, spring())
//        }
//
//        // Iniciar lluvia de confeti
//        confettiProgress.animateTo(1f, tween(2500, easing = EaseOutQuart))
//    }
//
//    BoxWithConstraints(
//        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = bgAlpha.value)),
//        contentAlignment = Alignment.Center
//    ) {
//        val width = maxWidth
//        val height = maxHeight
//
//        // --- CAPA DE AURA (Solo antes de la explosión) ---
//        if (currentStep < 2) {
//            Box(
//                Modifier.size(width * 0.8f)
//                    .graphicsLayer { rotationZ = auraRotation }
//                    .drawWithCache {
//                        onDrawBehind {
//                            drawCircle(
//                                brush = Brush.sweepGradient(
//                                    listOf(
//                                        Color.Transparent,
//                                        Color(0xFFFFD700),
//                                        Color.Transparent
//                                    )
//                                ),
//                                style = Stroke(width = 6.dp.toPx())
//                            )
//                        }
//                    }
//                    .blur(15.dp)
//            )
//        }
//
//        // --- CAPA CONFETI (Aparece tras la explosión) ---
//        if (currentStep == 2) {
//            LevelUpConfetti(confettiProgress.value)
//        }
//
//        // --- NÚCLEO VISUAL ---
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.offset(x = shakeOffset.value.dp)
//        ) {
//            // CONTENEDOR DE IMÁGENES (AVATAR + RANGOS)
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.size(width * 0.65f)
//            ) {
//
//                // Foto de Usuario Fija
//                AsyncImage(
//                    model = userImageUrl,
//                    contentDescription = null,
//                    modifier = Modifier
//                        .fillMaxSize(0.7f)
//                        .clip(CircleShape)
//                        .border(3.dp, Color(0xFFFFD700).copy(alpha = 0.6f), CircleShape),
//                    contentScale = ContentScale.Crop
//                )
//
//                // RANGO ANTERIOR (Capa de explosión)
//                if (oldElementAlpha.value > 0.01f) {
//                    Image(
//                        painter = painterResource(oldRankRes),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(110.dp)
//                            .align(Alignment.BottomEnd)
//                            .graphicsLayer {
//                                scaleX = oldElementScale.value
//                                scaleY = oldElementScale.value
//                                alpha = oldElementAlpha.value
//                            }
//                    )
//                }
//
//                // RANGO NUEVO (Capa de aparición)
//                if (currentStep == 2) {
//                    Image(
//                        painter = painterResource(newRankRes),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(120.dp)
//                            .align(Alignment.BottomEnd)
//                            .graphicsLayer {
//                                scaleX = newElementScale.value
//                                scaleY = newElementScale.value
//                                alpha = newElementAlpha.value
//                            }
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(height * 0.06f))
//
//            // ÁREA DE TEXTOS
//            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
//                // Texto Viejo que se expande
//                if (oldElementAlpha.value > 0.1f) {
//                    Text(
//                        previousLevelName.uppercase(),
//                        color = Color.White,
//                        fontSize = 32.sp,
//                        fontWeight = FontWeight.ExtraBold,
//                        modifier = Modifier.graphicsLayer {
//                            scaleX = oldElementScale.value * 0.6f
//                            alpha = oldElementAlpha.value
//                        }
//                    )
//                }
//
//                // Texto Nuevo que aparece
//                if (currentStep == 2) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            "¡NUEVO RANGO!",
//                            color = Color.White.copy(alpha = 0.7f),
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Bold,
//                            letterSpacing = 4.sp
//                        )
//                        Text(
//                            newLevelName.uppercase(),
//                            color = Color(0xFFFFD700),
//                            fontSize = 44.sp,
//                            fontWeight = FontWeight.Black,
//                            modifier = Modifier.graphicsLayer {
//                                scaleX = newElementScale.value
//                                scaleY = newElementScale.value
//                            }
//                        )
//                    }
//                }
//            }
//        }
//
//        // --- CAPA DE FLASH BLANCO ---
//        Box(
//            Modifier.fillMaxSize()
//                .background(Color.White.copy(alpha = flashAlpha.value))
//                .zIndex(10f)
//        )
//
//        // --- BOTÓN DE CIERRE ---
//        if (currentStep == 2) {
//            Button(
//                onClick = {},
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(bottom = height * 0.06f)
//                    .fillMaxWidth(0.8f)
//                    .height(56.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5fb4)),
//                shape = RoundedCornerShape(12.dp),
//                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
//            ) {
//                Text("RECLAMAR RECOMPENSA", fontWeight = FontWeight.ExtraBold)
//            }
//        }
//    }
//}
//
//@Composable
//fun LevelUpConfetti(progress: Float) {
//    // Generamos las partículas una sola vez
//    val particles = remember {
//        List(70) {
//            Triple(
//                Random.nextFloat(), // x inicial
//                Random.nextFloat(), // y inicial
//                listOf(Color.Yellow, Color.Cyan, Color.White, Color(0xFFFF4081)).random() // color
//            )
//        }
//    }
//
//    Canvas(Modifier.fillMaxSize()) {
//        particles.forEach { (x, y, color) ->
//            // Simula caída de arriba hacia abajo basada en el progreso
//            val currentY = (y + progress * 1.5f) % 1f
//            val alpha = (1f - progress).coerceIn(0f, 1f)
//
//            drawCircle(
//                color = color,
//                radius = 7f,
//                center = Offset(x * size.width, currentY * size.height),
//                alpha = alpha
//            )
//        }
//    }
//}