package pe.breaker.dkaviplay.presentation.screen.login

import pe.breaker.dkaviplay.domain.model.LoginResult

data class LoginState (
    val usuario: String? = null,
    val password: String? = null,

    val usuarioError: String? = null,
    val passwordError: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    val navigationResult: LoginResult? = null
)