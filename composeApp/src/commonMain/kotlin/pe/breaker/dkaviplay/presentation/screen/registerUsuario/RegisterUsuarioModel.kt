package pe.breaker.dkaviplay.presentation.screen.registerUsuario

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.data.util.PasswordRequirements
import pe.breaker.dkaviplay.domain.usecase.RegisterUseCase

class RegisterUsuarioModel(
    private val registerUseCase: RegisterUseCase,
) : StateScreenModel<RegisterUsuarioState>(RegisterUsuarioState()) {

    private inline fun updateState(
        crossinline block: RegisterUsuarioState.() -> RegisterUsuarioState
    ) {
        mutableState.update { it.block() }
    }

    fun onFieldChanged(newValue: String, update: (RegisterUsuarioState, String) -> RegisterUsuarioState) {
        mutableState.update { currentState ->
            update(currentState, newValue)
        }
    }

    fun onRegisterUsuario() {
        val s = state.value

        val usuario = s.usuario.orEmpty()
        val password = s.password.orEmpty()
        val confirmPassword = s.confirmPassword.orEmpty()
        val passwordRequirements = PasswordRequirements.from(password)

        val usuarioErr = if (usuario.isBlank()) "El usuario es obligatorio" else null
        val passwordErr = when {
            password.isBlank() -> "La contraseña es obligatoria"
            !passwordRequirements.isValid -> "La contraseña no cumple con los requisitos de seguridad"
            else -> null
        }
        val confirmErr = when {
            confirmPassword.isBlank() -> "La confirmación es obligatoria"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }

        updateState {
            copy(
                usuarioError = usuarioErr,
                passwordError = passwordErr,
                confirmPasswordError = confirmErr,
                errorMessage = null
            )
        }

        if (usuarioErr != null || passwordErr != null || confirmErr != null) return

        screenModelScope.launch {
            updateState { copy(isLoading = true) }

            registerUseCase(usuario, password)
                .onSuccess { uid ->
                    updateState {
                        copy(
                            isLoading = false,
                            isStepOneSuccess = true,
                            usuarioUid = uid
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }
}