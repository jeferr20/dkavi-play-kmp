package pe.breaker.dkaviplay.presentation.screen.registerUsuario

data class RegisterUsuarioState(
    val usuarioUid: Int? = null,
    val isLoading: Boolean = false,
    val isLoadingSedes: Boolean = false,
    val isStepOneSuccess: Boolean = false,
    val isStepTwoSuccess: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    val usuario: String? = null,
    val password: String? = null,
    val confirmPassword: String? = null,

    val usuarioError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
)