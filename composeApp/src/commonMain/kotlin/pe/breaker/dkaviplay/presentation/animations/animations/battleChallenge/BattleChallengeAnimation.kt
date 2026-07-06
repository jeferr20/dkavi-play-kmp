package pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.core.domain.animations.ChallengeSpark
import pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge.components.ChallengeLightningEffect
import pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge.components.ChallengePlayerCard
import pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge.components.ItemSheet
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory
import pe.breaker.dkaviplay.presentation.components.button.ActionIconButton
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import kotlin.math.PI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleChallengeAnimation(
    modifier: Modifier,
    playerOneName: String,
    playerTwoName: String,
    playerOneUrl: String?,
    playerTwoUrl: String?,
    playerOneRank: DrawableResource,
    playerTwoRank: DrawableResource,
    typeGame: String,
    onArbitro : () -> Unit,
    onAcuerdoMutuo : () -> Unit,
    hideButtonTerminar: Boolean,
    onShare: () -> Unit = {}
) {
    val audioFactory: AudioFactory = koinInject()

    val sparks = remember { mutableStateListOf<ChallengeSpark>() }
    var showSparks by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    // Animaciones principales
    val p1OffsetY = remember { Animatable(-400f) }
    val p2OffsetY = remember { Animatable(400f) }

    val p1Scale = remember { Animatable(0.8f) }
    val p2Scale = remember { Animatable(0.8f) }

    val vsAlpha = remember { Animatable(0f) }
    val vsScale = remember { Animatable(0f) }

    // 💥 SHAKE
    val shake = remember { Animatable(0f) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    audioFactory.stopBattleMusic()
                }

                Lifecycle.Event.ON_STOP -> {
                    audioFactory.stopBattleMusic()
                }

                Lifecycle.Event.ON_RESUME -> {
                    audioFactory.playBattleMusic()
                }

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
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = shake.value
            },
        contentAlignment = Alignment.Center
    ) {

        val screenHeight = constraints.maxHeight.toFloat()
        val separation = screenHeight * 0.22f

        LaunchedEffect(Unit) {
            audioFactory.playBattleMusic()

            delay(350)

            val impactPoint = 0f

            // 1️⃣ ENTRADA AL CENTRO (CHOQUE)
            launch {
                p1OffsetY.animateTo(
                    impactPoint,
                    tween(350, easing = FastOutLinearInEasing)
                )
            }

            launch {
                p2OffsetY.animateTo(
                    impactPoint,
                    tween(350, easing = FastOutLinearInEasing)
                )
            }

            delay(350)

            // 2️⃣ IMPACTO
            showSparks = true

            repeat(30) { i ->
                sparks.add(
                    ChallengeSpark(
                        i,
                        (0..360).random() * (PI / 180.0),
                        (150..400).random().toFloat()
                    )
                )
            }

            // VS animación
            launch {
                vsAlpha.animateTo(1f, tween(100))
                vsScale.animateTo(1.8f, tween(120))
                vsScale.animateTo(1f, spring(Spring.DampingRatioHighBouncy))
            }

            // Rebote jugadores
            launch {
                p1Scale.animateTo(1.1f, tween(100))
                p1Scale.animateTo(0.95f, tween(80))
                p1Scale.animateTo(1f, spring())
            }

            launch {
                p2Scale.animateTo(1.1f, tween(100))
                p2Scale.animateTo(0.95f, tween(80))
                p2Scale.animateTo(1f, spring())
            }

            // 💥 SHAKE (vibración impacto)
            launch {
                shake.animateTo(15f, tween(40))
                shake.animateTo(-15f, tween(40))
                shake.animateTo(10f, tween(40))
                shake.animateTo(-10f, tween(40))
                shake.animateTo(0f, tween(40))
            }

            delay(200)

            // 3️⃣ SEPARACIÓN FINAL
            launch {
                p1OffsetY.animateTo(
                    -separation,
                    spring(stiffness = Spring.StiffnessLow)
                )
            }

            launch {
                p2OffsetY.animateTo(
                    separation,
                    spring(stiffness = Spring.StiffnessLow)
                )
            }

            delay(700)
            showButton = true
        }

        // 🔷 Título
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = maxHeight * 0.03f)
                .zIndex(5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xFF1A237E).copy(alpha = 0.85f),
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, Color(0xFF3F51B5)),
                shadowElevation = 6.dp
            ) {
                Text(
                    text = "MODO RETO: ${typeGame.uppercase()}",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // ⚡ Sparks
        if (showSparks) {
            Box(Modifier.zIndex(1f)) {
                sparks.forEach { ChallengeLightningEffect(it) }
            }
        }

        // 🆚 VS
        Box(
            modifier = Modifier
                .size(75.dp)
                .graphicsLayer {
                    alpha = vsAlpha.value
                    scaleX = vsScale.value
                    scaleY = vsScale.value
                }
                .zIndex(3f)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF3F51B5), Color(0xFF1A237E))
                    )
                )
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "VS",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp
            )
        }

        // 🔴 Player 1
        ChallengePlayerCard(
            name = playerOneName,
            url = playerOneUrl,
            rankRes = playerOneRank,
            tag = "RETADOR",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.75f)
                .graphicsLayer {
                    translationY = p1OffsetY.value
                    scaleX = p1Scale.value
                    scaleY = p1Scale.value
                }
                .zIndex(4f)
        )

        // 🔵 Player 2
        ChallengePlayerCard(
            name = playerTwoName,
            url = playerTwoUrl,
            rankRes = playerTwoRank,
            tag = "RETADO",
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.75f)
                .graphicsLayer {
                    translationY = p2OffsetY.value
                    scaleX = p2Scale.value
                    scaleY = p2Scale.value
                }
                .zIndex(4f)
        )

        if(showButton && !hideButtonTerminar){
            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.85f)
                    .zIndex(6f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                CustomButtonFilled(
                    modifier = Modifier.weight(1f),
                    enabled = showButton,
                    onClick = { showSheet = true },
                    text = "FINALIZAR PARTIDA"
                )

                ActionIconButton(
                    icon = Icons.Default.Share,
                    contentDescription = "Compartir",
                    onClick = onShare,
                    colorBackground = Color.Black,
                    colorIcon = Color.White,
                )
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = colorBlackSurface,
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Finalizar partida",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    ItemSheet(
                        onclick = {
                            showSheet = false
                            audioFactory.stopBattleMusic()
                            onArbitro()
                        },
                        iconoString = "⚖️",
                        titulo = "Finalizar por árbitro",
                        descripcion = "Un tercero decide el resultado",
                    )

                    ItemSheet(
                        onclick = {
                            showSheet = false
                            audioFactory.stopBattleMusic()
                            onAcuerdoMutuo()
                        },
                        iconoString = "🤝",
                        titulo = "Finalizar por acuerdo mutuo",
                        descripcion = "Ambos jugadores aceptan el resultado",
                    )

                    // Cancelar (UX importante)
                    TextButton(
                        onClick = { showSheet = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Cancelar", color = colorPrimary, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}