package pe.breaker.dkaviplay.presentation.screen.forgetPassword.step1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun Step1(
    screenModel: ForgetPasswordModel,
    state: ForgetPasswordState
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Recuperar contraseña",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Valida tus datos para enviarte un código de recuperación a tu correo.",
            color = Color.Gray,
            fontSize = 14.sp
        )

        FormTextField(
            label = "Ingrese su Correo registrado",
            value = state.correo ?: "",
            icon = Icons.Default.Mail,
            onValueChange = { screenModel.onEmailChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "ejemplo@correo.com",
            isPassword = false,
            inputType = InputType.CORREO,
            error = state.correoError,
        )

        FormTextField(
            label = "Ingrese su celular",
            value = state.phone ?: "",
            icon = Icons.Default.Phone,
            onValueChange = { screenModel.onPhoneDigitsChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "XXXX",
            isPassword = false,
            inputType = InputType.CELULAR,
            error = state.phoneError,
        )

        Spacer(Modifier.weight(1f))

        CustomButtonFilled(
            onClick = { screenModel.requestReset() },
            enabled = !state.isLoading,
            text = "Enviar código al correo"
        )

        Spacer(Modifier.height(16.dp))
    }
}