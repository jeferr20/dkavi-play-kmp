package pe.breaker.dkaviplay.presentation.screen.resultadosPartida

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dkaviplay.composeapp.generated.resources.Res
import dkaviplay.composeapp.generated.resources.Trofeo
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.presentation.animations.animations.battleLoser.BattleLoserAnimation
import pe.breaker.dkaviplay.presentation.animations.animations.battleWinner.BattleWinnerAnimation
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.shareable.winnerShareable.CaptureWinnerShareable
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper
import pe.breaker.dkaviplay.presentation.util.ShareHandler

class ResultadoPartidaScreen(private val reservaUid: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ResultadoPartidaModel>(
            parameters = { parametersOf(reservaUid) }
        )
        val state by screenModel.state.collectAsState()

        val shareHandler = koinInject<ShareHandler>()
        var triggerCapture by remember { mutableStateOf(false) }

        if (triggerCapture) {
            CaptureWinnerShareable(
                state = state,
                onCaptured = { bitmap ->
                    shareHandler.shareBitmap(bitmap)
                    triggerCapture = false
                }
            )
        }

        Scaffold(
            containerColor = Color.Black,
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                when {
                    state.isLoading -> LoadingDialog()

                    (state.isSuccess && state.ganador != null) -> {
                        val soyGanador = state.currentUserId == state.ganador?.userUid

                        if (soyGanador) {
                            BattleWinnerAnimation(
                                modifier = Modifier.fillMaxSize(),
                                winnerName = state.ganador?.usuario ?: "USER NO DISPONIBLE",
                                loserName = state.perdedor?.usuario ?: "USER NO DISPONIBLE",
                                winnerProfileUrl = state.ganador?.imagen ?: "",
                                loserProfileUrl = state.perdedor?.imagen ?: "",
                                winnerRankRes = RankResourceMapper.getDrawableByRank(state.ganador?.rango),
                                loserRankRes = RankResourceMapper.getDrawableByRank(state.perdedor?.rango),
                                trophyRes = Res.drawable.Trofeo,
                                onShare = {
                                    triggerCapture = true
                                }
                            )

                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.TopCenter)) {
                                CustomAppbar(
                                    onClick = { navigator.pop() }
                                )
                            }
                        } else {
                            BattleLoserAnimation(
                                winnerName = state.ganador?.usuario ?: "USER NO DISPONIBLE",
                                loserName = state.perdedor?.usuario ?: "USER NO DISPONIBLE",
                                winnerProfileUrl = state.ganador?.imagen ?: "",
                                loserProfileUrl = state.perdedor?.imagen ?: "",
                                winnerRankRes = RankResourceMapper.getDrawableByRank(state.ganador?.rango),
                                loserRankRes = RankResourceMapper.getDrawableByRank(state.perdedor?.rango),
                            )
                            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).align(Alignment.TopCenter)) {
                                CustomAppbar(
                                    onClick = { navigator.pop() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}