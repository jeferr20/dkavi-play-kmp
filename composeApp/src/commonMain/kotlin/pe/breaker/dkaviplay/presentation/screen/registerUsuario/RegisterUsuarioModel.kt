package pe.breaker.dkaviplay.presentation.screen.registerUsuario

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.presentation.screen.registerUsuario.RegisterUsuarioState
import pe.breaker.dkaviplay.domain.usecase.auth.RegisterUseCase

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

        updateState {
            copy(
                usuarioError = if (usuario.isBlank()) "El correo es obligatorio" else null,
                passwordError = if (password.isBlank()) "La contraseña es obligatoria" else null,
                confirmPasswordError = if (confirmPassword.isBlank()) "La confirmación es obligatoria" else null,
                errorMessage = null
            )
        }

        if (usuario.isBlank() || password.isBlank() || confirmPassword.isBlank()) return

        if (password != confirmPassword) {
            updateState {
                copy(confirmPasswordError = "Las contraseñas no coinciden")
            }
            return
        }

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