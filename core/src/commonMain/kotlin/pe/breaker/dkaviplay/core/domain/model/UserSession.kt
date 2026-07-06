package pe.breaker.dkaviplay.core.domain.model

data class UserSession (
    val uid: String,
    val role: String,
    val token: String,
    val cripKey: String
)