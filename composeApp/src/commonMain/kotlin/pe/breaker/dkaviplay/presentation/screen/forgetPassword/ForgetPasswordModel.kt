package pe.breaker.dkaviplay.presentation.screen.forgetPassword

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.usecase.auth.SendCodeUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.UpdatePasswordUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.VerifyCodeUseCase
import pe.breaker.dkaviplay.util.isValidEmail
import pe.breaker.dkaviplay.util.isValidPhone

class ForgetPasswordModel(
    private val sendCodeUseCase: SendCodeUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val verifyCodeUseCase: VerifyCodeUseCase
) : StateScreenModel<ForgetPasswordState>(ForgetPasswordState()) {
    private var timerJob: Job? = null

    fun onNewPasswordChange(newPassword: String) {
        mutableState.update { it.copy(newPassword = newPassword, newPasswordError = null) }
    }

    fun onConfirmNewPasswordChange(newPassword: String) {
        mutableState.update { it.copy(newPasswordConfirm = newPassword, newPasswordConfirmError = null) }
    }

    fun onEmailChanged(email: String) {
        mutableState.update { it.copy(correo = email, correoError = null) }
    }

    fun onPhoneDigitsChanged(digits: String) {
        if (digits.length <= 9) {
            mutableState.update { it.copy(phone = digits, phoneError = null) }
        }
    }

    fun clearError(){
        mutableState.update { it.copy(errorMessage = null,codeError = null) }
    }

    fun onCodeChanged(newCode: String) {
        val onlyNumbers = newCode.filter { it.isDigit() }
        if (onlyNumbers.length <= 6) {
            mutableState.update { it.copy(code = onlyNumbers, codeError = null) }

            if (onlyNumbers.length == 6) {
                verifyCode()
            }
        }
    }

    fun requestReset() {
        val currentState = state.value
        val isEmailInvalid = currentState.correo.isNullOrBlank() || !currentState.correo.isValidEmail()
        val isPhoneInvalid = currentState.phone.isNullOrBlank() || !currentState.phone.isValidPhone()

        if (isEmailInvalid || isPhoneInvalid) {
            mutableState.update {
                it.copy(
                    correoError = if (isEmailInvalid) "Ingresa un correo válido" else null,
                    phoneError = if (isPhoneInvalid) "Ingresa un celular válido" else null
                )
            }
            return
        }

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = sendCodeUseCase(currentState.correo!!, currentState.phone!!)

            result.onSuccess { uid ->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        userUid = uid,
                        step = 1,
                        timerSeconds = 60,
                        canResendCode = false
                    )
                }
                startResendTimer()
            }.onFailure { e ->
                mutableState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun verifyCode() {
        val currentState = state.value
        if (currentState.userUid == null || currentState.code.length != 6) return
        println("CODIGO: ${currentState.code}")
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, codeError = null) }

            val result = verifyCodeUseCase(currentState.userUid, currentState.code)

            result.onSuccess { isValid ->
                if (isValid) {
                    mutableState.update { it.copy(isLoading = false, step = 2) } // Paso 2: Nueva Clave
                } else {
                    mutableState.update { it.copy(isLoading = false, codeError = "Código inválido o expirado") }
                }
            }.onFailure { e ->
                mutableState.update { it.copy(isLoading = false, codeError = e.message) }
            }
        }
    }

    fun updatePassword() {
        val currentState = state.value
        val uid = currentState.userUid ?: return
        val pass = currentState.newPassword ?: ""
        val confirm = currentState.newPasswordConfirm ?: ""

        val isPassShort = pass.length < 6
        val isNotMatching = pass != confirm

        if (isPassShort || isNotMatching) {
            mutableState.update {
                it.copy(
                    newPasswordError = if (isPassShort) "Mínimo 6 caracteres" else null,
                    newPasswordConfirmError = if (isNotMatching) "Las contraseñas no coinciden" else null
                )
            }
            return
        }

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = updatePasswordUseCase(uid, pass)

            result.onSuccess { msg ->
                mutableState.update { it.copy(isLoading = false, successMessage = msg) }
            }.onFailure { e ->
                mutableState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun resetStep() {
        timerJob?.cancel()
        mutableState.update { it.copy(step = 0, code = "", codeError = null, errorMessage = null, userUid = null) }
    }

    private fun startResendTimer() {
        timerJob?.cancel()
        timerJob = screenModelScope.launch {
            while (state.value.timerSeconds > 0) {
                delay(1000)
                mutableState.update { it.copy(timerSeconds = it.timerSeconds - 1) }
            }
            mutableState.update { it.copy(canResendCode = true) }
        }
    }

    override fun onDispose() {
        timerJob?.cancel()
        super.onDispose()
    }
}