package pe.breaker.dkaviplay.presentation.screen.login

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.presentation.component.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.component.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.component.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.component.form.FormTextField
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordScreen
import pe.breaker.dkaviplay.presentation.screen.login.components.IconHeader
import pe.breaker.dkaviplay.presentation.screen.login.components.NoAccount
import pe.breaker.dkaviplay.presentation.screen.registerUsuario.RegisterUsuarioScreen
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.InputType
import pe.breaker.dkaviplay.presentation.util.StatusUiType

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<LoginModel>()
        val state by screenModel.state.collectAsState()

        val shakeOffset = remember { Animatable(0f) }
        val scrollState = rememberScrollState()

        val infiniteTransition = rememberInfiniteTransition()
        val floatAnim by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            )
        )

        LaunchedEffect(state.isSuccess) {
            if (state.isSuccess) {
                //navigator.replaceAll(SplashScreen())
            }
        }

        LaunchedEffect(state.errorMessage) {
            if (state.errorMessage != null) {
                repeat(5) {
                    shakeOffset.animateTo(15f, tween(40))
                    shakeOffset.animateTo(-15f, tween(40))
                }
                shakeOffset.animateTo(0f)
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
                    IconHeader(
                        modifier = Modifier.fillMaxWidth().height(380.dp),
                        floatAnim = floatAnim
                    )
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .padding(horizontal = 32.dp)
                            .offset(x = shakeOffset.value.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        FormTextField(
                            label = "Usuario",
                            value = state.usuario ?: "",
                            icon = Icons.Default.Person,
                            inputType = InputType.TEXTO,
                            onValueChange = {
                                screenModel.onFieldChanged(it) { s, v ->
                                    s.copy(
                                        usuario = v,
                                        usuarioError = null
                                    )
                                }
                            },
                            error = state.usuarioError
                        )

                        Column {
                            FormTextField(
                                label = "Contraseña",
                                value = state.password ?: "",
                                icon = Icons.Default.Lock,
                                inputType = InputType.PASSWORD,
                                placeholder = "************",
                                isPassword = true,
                                onValueChange = {
                                    screenModel.onFieldChanged(it) { s, v ->
                                        s.copy(
                                            password = v,
                                            passwordError = null
                                        )
                                    }
                                },
                                error = state.passwordError
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Olvidé mi contraseña",
                                color = colorPrimary.copy(alpha = 0.9f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 10.dp)
                                    .clickable { navigator.push(ForgetPasswordScreen()) }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        CustomButtonFilled(
                            onClick = { screenModel.onLogin() },
                            enabled = !state.isLoading,
                            text = "INICIAR SESIÓN"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    NoAccount(
                        modifier = Modifier,
                        onclick = { navigator.push(RegisterUsuarioScreen()) }
                    )
                }

                if (state.isLoading) LoadingDialog()

                state.errorMessage?.let {
                    StatusDialog(
                        status = StatusUiType.ERROR,
                        message = it,
                        onDismiss = { screenModel.clearError() }
                    )
                }
            }
        }
    }
}