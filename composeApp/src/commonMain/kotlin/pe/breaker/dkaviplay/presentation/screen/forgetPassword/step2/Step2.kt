package pe.breaker.dkaviplay.presentation.screen.forgetPassword.step2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordModel
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordState
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.step2.components.OtpInputField

@Composable
fun Step2(
    screenModel: ForgetPasswordModel,
    state: ForgetPasswordState
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.3f))

        // --- CABECERA ---
        Icon(
            imageVector = Icons.Default.MarkEmailRead,
            contentDescription = null,
            tint = Color(0xFF2196F3),
            modifier = Modifier.size(70.dp)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Verifica tu correo",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Ingresa el código de 6 dígitos",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(40.dp))

        // --- OTP INPUT ---
        OtpInputField(
            code = state.code,
            onCodeChange = { screenModel.onCodeChanged(it) }
        )

        Spacer(Modifier.height(30.dp))

        // --- REENVÍO ---
        Text(
            text = if (state.canResendCode) "¿No recibiste el código?"
            else "Reenviar en ${state.timerSeconds}s",
            color = Color.Gray,
            fontSize = 14.sp
        )
        if (state.canResendCode) {
            TextButton(onClick = { screenModel.requestReset() }) {
                Text(
                    "Reenviar nuevo código",
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // --- ACCIONES ---
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CustomButtonFilled(
                onClick = {
                    focusManager.clearFocus()
                    screenModel.verifyCode()
                },
                enabled = !state.isLoading && state.code.length == 6,
                text = "Verificar código"
            )

            TextButton(onClick = { screenModel.resetStep() }) {
                Text(
                    "Volver a editar correo",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}