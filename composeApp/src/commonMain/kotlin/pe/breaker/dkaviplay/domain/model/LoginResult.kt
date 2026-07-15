package pe.breaker.dkaviplay.domain.model

sealed interface LoginResult {
    data class Success(val session: UserSession) : LoginResult
    data class Incomplete(val usuarioId: Int) : LoginResult
}