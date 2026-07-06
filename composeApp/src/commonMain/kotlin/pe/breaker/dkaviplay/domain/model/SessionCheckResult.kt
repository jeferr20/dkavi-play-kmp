package pe.breaker.dkaviplay.domain.model

sealed interface SessionCheckResult {
    object NoSession : SessionCheckResult
    data class Authenticated(val userId: String) : SessionCheckResult
    data class NetworkError(val message: String) : SessionCheckResult
}