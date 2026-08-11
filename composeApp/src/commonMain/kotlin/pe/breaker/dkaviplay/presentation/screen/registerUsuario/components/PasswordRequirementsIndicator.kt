package pe.breaker.dkaviplay.presentation.screen.registerUsuario.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.data.util.PasswordRequirements

@Composable
fun PasswordRequirementsIndicator(
    requirements: PasswordRequirements,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        RequirementItem(
            text = "Mínimo 8 caracteres",
            isSatisfied = requirements.hasMinLength
        )
        RequirementItem(
            text = "Al menos una letra mayúscula",
            isSatisfied = requirements.hasUppercase
        )
        RequirementItem(
            text = "Al menos un número",
            isSatisfied = requirements.hasNumber
        )
        RequirementItem(
            text = "Al menos un carácter especial (@, #, $, etc.)",
            isSatisfied = requirements.hasSpecialChar
        )
    }
}