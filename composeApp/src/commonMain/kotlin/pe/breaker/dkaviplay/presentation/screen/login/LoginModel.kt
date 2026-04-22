package pe.breaker.dkaviplay.presentation.screen.login

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.usecase.LoginUseCase

class LoginModel(
    private val loginUseCase: LoginUseCase,
//    private val notificationRepository: NotificationRepository
) : StateScreenModel<LoginState>(LoginState()) {

    fun onFieldChanged(newValue: String, update: (LoginState, String) -> LoginState) {
        mutableState.update { currentState ->
            update(currentState, newValue)
        }
    }

    fun onLogin(){
        mutableState.update { it.copy(usuarioError = null, passwordError = null, errorMessage = null) }
        val usuario = mutableState.value.usuario?.trim() ?: ""
        val pass = mutableState.value.password ?: ""

        if (usuario.isEmpty() || pass.isEmpty()) {
            mutableState.update {
                it.copy(
                    usuarioError = if (usuario.isEmpty()) "El correo es obligatorio" else null,
                    passwordError = if (pass.isEmpty()) "La contraseña es obligatoria" else null
                )
            }
            return
        }

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val result = loginUseCase(usuario, pass)
            result.onSuccess {
//                try {
//                    val token = Firebase.messaging.getToken()
//                    notificationRepository.saveToken(token)
//                } catch (e: Exception) {
//                    // Logueamos pero no bloqueamos el login por un error de push
//                    println("Error guardando token en login: ${e.message}")
//                }

                mutableState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }
}