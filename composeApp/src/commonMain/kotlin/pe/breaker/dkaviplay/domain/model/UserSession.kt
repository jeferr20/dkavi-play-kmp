package pe.breaker.dkaviplay.domain.model

data class UserSession (
    val userId:Int,
    val uid: String,
    val role: String,
    val token: String,
    val cripKey: String
)