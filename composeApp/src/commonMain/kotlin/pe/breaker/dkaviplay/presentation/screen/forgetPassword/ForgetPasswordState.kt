package pe.breaker.dkaviplay.presentation.screen.forgetPassword

data class ForgetPasswordState(
    val phone: String? = null,
    val phoneError: String? = null,
    val correo: String? = null,
    val correoError: String? = null,

    val userId: Int? = null,
    val code: String = "",
    val codeError: String? = null,

    val newPassword:String? = null,
    val newPasswordConfirm:String? = null,
    val newPasswordError:String? = null,
    val newPasswordConfirmError:String? = null,

    val step: Int = 0, // 0: Formulario Inicial, 1: Ingreso de Código, 2: Ingreso de nueva contraseña
    val isLoading: Boolean = false,
    val canResendCode: Boolean = false, // Para habilitar el botón de reenvío
    val timerSeconds: Int = 60,

    val errorMessage: String? = null,
    val successMessage: String? = null
)