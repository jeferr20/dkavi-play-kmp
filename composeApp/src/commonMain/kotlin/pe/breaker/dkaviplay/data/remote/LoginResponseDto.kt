package pe.breaker.dkaviplay.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto (
    val token: String,
    val firebaseToken: String,
    val cripKey: String
)