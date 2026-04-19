package pe.breaker.dkaviplay.presentation.screen.forgetPassword.step3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.component.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.component.form.FormTextField
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordModel
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordState
import pe.breaker.dkaviplay.presentation.util.InputType

@Composable
fun Step3(
    screenModel: ForgetPasswordModel,
    state: ForgetPasswordState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Nueva contraseña",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Crea una clave segura que no hayas usado antes.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            FormTextField(
                label = "Contraseña",
                value = state.newPassword ?: "",
                icon = Icons.Default.Lock,
                inputType = InputType.PASSWORD,
                placeholder = "************",
                isPassword = true,
                onValueChange = { screenModel.onNewPasswordChange(it) },
                error = state.newPasswordError
            )

            FormTextField(
                label = "Confirmar Contraseña",
                value = state.newPasswordConfirm ?: "",
                icon = Icons.Default.Lock,
                inputType = InputType.PASSWORD,
                placeholder = "************",
                isPassword = true,
                onValueChange = { screenModel.onConfirmNewPasswordChange(it) },
                error = state.newPasswordConfirmError
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButtonFilled(
                onClick = {
                    screenModel.updatePassword()
                },
                enabled = !state.isLoading && !state.newPassword.isNullOrBlank() && state.newPassword == state.newPasswordConfirm,
                text = "Actualizar Contraseña"
            )
        }
    }
}