package pe.breaker.dkaviplay.presentation.screen.registerUsuario

import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.components.form.FormTextField
import pe.breaker.dkaviplay.presentation.screen.login.components.IconHeader
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosScreen
import pe.breaker.dkaviplay.presentation.screen.registerUsuario.components.AlreadyAccount
import pe.breaker.dkaviplay.presentation.util.InputType
import pe.breaker.dkaviplay.presentation.util.StatusUiType

class RegisterUsuarioScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<RegisterUsuarioModel>()
        val state by screenModel.state.collectAsState()

        val shakeOffset = remember { Animatable(0f) }
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

        LaunchedEffect(state.errorMessage) {
            if (state.errorMessage != null) {
                repeat(5) {
                    shakeOffset.animateTo(15f, tween(40))
                    shakeOffset.animateTo(-15f, tween(40))
                }
                shakeOffset.animateTo(0f)
            }
        }

        LaunchedEffect(state.isStepOneSuccess) {
            if (state.isStepOneSuccess) {
                navigator.replaceAll(RegisterDatosScreen(false, state.usuarioUid ?: ""))
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
                Column(modifier = Modifier.fillMaxSize().imePadding()
                    .navigationBarsPadding()) {
                    Box {
                        CustomAppbar(
                            modifier = Modifier.statusBarsPadding().padding(horizontal = 12.dp),
                            onClick = { navigator.pop() }
                        )

                        IconHeader(
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            floatAnim = floatAnim
                        )
                    }
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp)
                            .offset(x = shakeOffset.value.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        FormTextField(
                            label = "Usuario",
                            value = state.usuario ?: "",
                            icon = Icons.Default.Person,
                            onValueChange = { newValue ->
                                screenModel.onFieldChanged(newValue) { state, value ->
                                    state.copy(usuario = value, usuarioError = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "Tu nombre de usuario",
                            isPassword = false,
                            inputType = InputType.TEXTO,
                            error = state.usuarioError
                        )

                        FormTextField(
                            label = "Contraseña",
                            value = state.password ?: "",
                            icon = Icons.Default.Lock,
                            onValueChange = { newValue ->
                                screenModel.onFieldChanged(newValue) { state, value ->
                                    state.copy(password = value, passwordError = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "••••••••",
                            isPassword = true,
                            inputType = InputType.PASSWORD,
                            error = state.passwordError
                        )

                        FormTextField(
                            label = "Confirmar Contraseña",
                            value = state.confirmPassword ?: "",
                            icon = Icons.Default.Lock,
                            onValueChange = { newValue ->
                                screenModel.onFieldChanged(newValue) { state, value ->
                                    state.copy(
                                        confirmPassword = value,
                                        confirmPasswordError = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "Repite tu contraseña",
                            isPassword = true,
                            inputType = InputType.PASSWORD,
                            error = state.confirmPasswordError
                        )

                        CustomButtonFilled(
                            onClick = { screenModel.onRegisterUsuario() },
                            enabled = !state.isLoading,
                            text = "REGISTRAR"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    AlreadyAccount(
                        modifier = Modifier,
                        onclick = { navigator.pop() }
                    )
                }
            }
            if (state.isLoading) {
                LoadingDialog(message = "Creando usuario...")
            }

            state.errorMessage?.let { errorMsg ->
                StatusDialog(
                    status = StatusUiType.ERROR,
                    message = errorMsg,
                    onDismiss = { screenModel.clearError() }
                )
            }
        }
    }
}