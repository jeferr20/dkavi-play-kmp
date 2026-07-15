package pe.breaker.dkaviplay.presentation.screen.registerDatos

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.login.LoginScreen
import pe.breaker.dkaviplay.presentation.screen.login.components.IconHeader
import pe.breaker.dkaviplay.presentation.screen.registerDatos.component.DatosBillar
import pe.breaker.dkaviplay.presentation.screen.registerDatos.component.DatosPersonales
import pe.breaker.dkaviplay.presentation.screen.registerDatos.component.DatosUbigeo
import pe.breaker.dkaviplay.presentation.screen.registerDatos.component.InfoContacto
import pe.breaker.dkaviplay.presentation.util.StatusUiType

class RegisterDatosScreen(
    private val isLogged: Boolean,
    private val usuarioId: Int?,
) : Screen {
    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<RegisterDatosModel>(
            parameters = { parametersOf(isLogged,usuarioId) }
        )
        val state by screenModel.state.collectAsState()

        val scrollState = rememberScrollState()
        val infiniteTransition = rememberInfiniteTransition()
        val floatAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
        )

        var showExitDialog by remember { mutableStateOf(false) }
        val handleBackAction = {
            if (isLogged) {
                navigator.pop()
            } else {
                showExitDialog = true
            }
        }

        BackHandler(enabled = true) {
            handleBackAction()
        }

        LaunchedEffect(state.successMessage) {
            if (!state.successMessage.isNullOrEmpty() && !isLogged) {
                navigator.replaceAll(LoginScreen())
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            containerColor = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .navigationBarsPadding()
                ) {
                    Box {
                        CustomAppbar(
                            modifier = Modifier.statusBarsPadding().padding(horizontal = 12.dp),
                            onClick = { handleBackAction() }
                        )

                        IconHeader(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            floatAnim = floatAnim
                        )
                    }

                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        DatosPersonales(
                            modifier = Modifier.fillMaxWidth(),
                            state = state,
                            screenModel = screenModel,
                            isLogged = isLogged
                        )

                        InfoContacto(
                            modifier = Modifier.fillMaxWidth(),
                            state = state,
                            screenModel = screenModel,
                            isLogged = isLogged
                        )

                        DatosUbigeo(
                            modifier = Modifier.fillMaxWidth(),
                            state = state,
                            screenModel = screenModel
                        )

                        DatosBillar(
                            modifier = Modifier.fillMaxWidth(),
                            state = state,
                            screenModel = screenModel
                        )

                        CustomButtonFilled(
                            onClick = { screenModel.onRegisterPersona() },
                            enabled = !state.isLoading,
                            text = if (isLogged) "ACTUALIZAR DATOS" else "FINALIZAR REGISTRO"
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (state.isLoading) LoadingDialog()

                if (isLogged && state.successMessage?.isNotBlank() == true) {
                    StatusDialog(
                        status = StatusUiType.SUCCESS,
                        message = state.successMessage!!,
                        onDismiss = { screenModel.clearSuccessMessage() },
                        confirmButtonText = "Aceptar",
                        hideCancelar = true,
                        onConfirm = {
                            screenModel.clearSuccessMessage()
                            navigator.pop()
                        }
                    )
                }
                if (showExitDialog) {
                    StatusDialog(
                        status = StatusUiType.WARNING,
                        message = "¿Estás seguro de que deseas salir? Perderás el progreso de tu registro.",
                        onDismiss = { showExitDialog = false },
                        confirmButtonText = "Salir",
                        onConfirm = {
                            showExitDialog = false
                            navigator.replaceAll(LoginScreen())
                        }
                    )
                }
            }
        }
    }
}