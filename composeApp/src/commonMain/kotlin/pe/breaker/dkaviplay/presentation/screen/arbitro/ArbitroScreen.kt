package pe.breaker.dkaviplay.presentation.screen.arbitro

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.PlatformContext
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.arbitro.components.ScoreInputSection
import pe.breaker.dkaviplay.presentation.util.StatusUiType

class ArbitroScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ArbitroModel>()
        val state by screenModel.state.collectAsState()
        val platformContext = koinInject<PlatformContext>()
        val haptic = LocalHapticFeedback.current

        LaunchedEffect(state.errorEscaneoMessage) {
            if (state.errorEscaneoMessage != null) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }

        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.imePadding()
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                if (state.step == ArbitroStep.SCANNING && state.errorEscaneoMessage == null) {
                    //TODO()
//                    key(state.step) {
//                        LectorQR(
//                            modifier = Modifier.padding(innerPadding),
//                            context = platformContext.androidContext,
//                            onQrDetected = { screenModel.buscarReserva(it) }
//                        )
//                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    CustomAppbar(
                        text = if (state.step == ArbitroStep.SCANNING) "Escanear Mesa" else "Registrar Score",
                        onClick = { navigator.pop() }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedContent(
                        targetState = state.step,
                        transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) }
                    ){ step ->
                        when (step) {
                            ArbitroStep.SCANNING -> { /* Nada aquí, ya está de fondo */
                            }

                            ArbitroStep.REGISTERING -> {
                                ScoreInputSection(
                                    reserva = state.reserva!!,
                                    resultados = state.resultados,
                                    onValueChange = { setIndex, ganadorIndex ->
                                        screenModel.onActualizarResultado(setIndex, ganadorIndex)
                                    },
                                    onSend = {
                                        screenModel.enviarResultados()
                                    }
                                )
                            }
                        }
                    }
                }

                if (state.isLoading) LoadingDialog()

                if (state.errorEscaneoMessage != null || state.errorResultadosMessage != null) {
                    StatusDialog(
                        status = StatusUiType.ERROR,
                        message = state.errorEscaneoMessage ?: state.errorResultadosMessage ?: "Ocurrió un error inesperado",
                        confirmButtonText = "Reintentar",
                        onDismiss = { if(state.step == ArbitroStep.SCANNING) screenModel.resetError() else screenModel.resetErrorResultados()},
                        onConfirm = { if(state.step == ArbitroStep.SCANNING) screenModel.resetError() else screenModel.resetErrorResultados()}
                    )
                }
            }
        }
    }
}