package pe.breaker.dkaviplay.presentation.screen.forgetPassword

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.presentation.component.CustomAppbar
import pe.breaker.dkaviplay.presentation.component.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.component.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.step1.Step1
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.step2.Step2
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.step3.Step3
import pe.breaker.dkaviplay.presentation.screen.login.LoginScreen
import pe.breaker.dkaviplay.presentation.screen.login.components.IconHeader
import pe.breaker.dkaviplay.presentation.util.StatusUiType

class ForgetPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ForgetPasswordModel>()
        val state by screenModel.state.collectAsState()

        val infiniteTransition = rememberInfiniteTransition()
        val floatAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
        )

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = Color.Black
        ) {
            Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                // --- CABECERA ---
                Box {
                    CustomAppbar(
                        modifier = Modifier.statusBarsPadding().padding(horizontal = 12.dp),
                        onClick = { navigator.pop() }
                    )
                    IconHeader(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        floatAnim = floatAnim
                    )
                }

                // --- CONTENIDO ANIMADO ---
                // Quitamos el Scroll de aquí para que el weight funcione
                AnimatedContent(
                    targetState = state.step,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) }
                ) { currentStep ->
                    when(currentStep){
                        0 ->{
                            Step1(
                                screenModel = screenModel,
                                state = state
                            )
                        }

                        1 ->{
                            Step2(
                                screenModel = screenModel,
                                state = state
                            )
                        }

                        2->{
                            Step3(
                                screenModel = screenModel,
                                state = state
                            )
                        }
                    }
                }
            }

            if (state.isLoading) LoadingDialog()

            state.errorMessage?.let{
                StatusDialog(
                    status = StatusUiType.ERROR,
                    message = it,
                    hideCancelar = true,
                    onDismiss = { screenModel.clearError() }
                )
            }

            state.codeError?.let{
                StatusDialog(
                    status = StatusUiType.ERROR,
                    message = it,
                    hideCancelar = true,
                    onDismiss = { screenModel.clearError() }
                )
            }

            state.successMessage?.let{
                StatusDialog(
                    status = StatusUiType.SUCCESS,
                    message = it,
                    hideCancelar = true,
                    onDismiss = {
                        screenModel.clearError()
                        navigator.replaceAll(LoginScreen())
                    }
                )
            }
        }
    }
}