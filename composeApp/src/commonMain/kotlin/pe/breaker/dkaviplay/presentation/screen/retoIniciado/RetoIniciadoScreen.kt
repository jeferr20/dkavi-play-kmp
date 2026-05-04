package pe.breaker.dkaviplay.presentation.screen.retoIniciado

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge.BattleChallengeAnimation
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo.AcuerdoMutuoScreen
import pe.breaker.dkaviplay.presentation.screen.retoIniciado.components.PartidaQrDialog
import pe.breaker.dkaviplay.presentation.shareable.battleShareable.CaptureBattleShareable
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper
import pe.breaker.dkaviplay.presentation.util.ShareHandler

class RetoIniciadoScreen(val reservaId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<RetoIniciadoModel>(
            parameters = { parametersOf(reservaId) }
        )
        val state by model.state.collectAsState()
        val isReady = state.retador != null && state.retado != null
        var showQr by remember { mutableStateOf(false) }

        val shareHandler = koinInject<ShareHandler>()
        var triggerCapture by remember { mutableStateOf(false) }

        if (triggerCapture) {
            CaptureBattleShareable(
                state = state,
                onCaptured = { bitmap ->
                    shareHandler.shareBitmap(bitmap)
                    triggerCapture = false
                }
            )
        }

        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.imePadding()
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {

                CustomAppbar(onClick = { navigator.pop() })

                if (isReady) {
                    BattleChallengeAnimation(
                        modifier = Modifier.fillMaxSize(),
                        playerOneName = state.retador?.usuario ?: "USER NO DISPONIBLE",
                        playerTwoName = state.retado?.usuario ?: "USER NO DISPONIBLE",
                        playerOneUrl = state.retador?.imagen,
                        playerTwoUrl = state.retado?.imagen,
                        playerOneRank = RankResourceMapper.getDrawableByRank(state.retador?.rango),
                        playerTwoRank = RankResourceMapper.getDrawableByRank(state.retado?.rango),
                        typeGame = state.reserva?.tipoJuego ?: "POOL",
                        onArbitro = { showQr = true },
                        onAcuerdoMutuo = {
                            state.reserva?.reservaUid?.let {
                                navigator.push(AcuerdoMutuoScreen(it))
                            }
                        },
                        hideButtonTerminar = state.reserva?.esperandoConfirmacion == true,
                        onShare = {
                            triggerCapture = true
                        }
                    )
                }
            }
            if (state.isLoading) {
                LoadingDialog()
            }
            if (showQr) {
                PartidaQrDialog(
                    player1 = state.retador?.usuario ?: "USER",
                    player2 = state.retado?.usuario ?: "USER",
                    qrText = state.reserva?.reservaUid,
                    onDismiss = {
                        showQr = false
                        navigator.pop()
                    }
                )
            }
        }
    }
}