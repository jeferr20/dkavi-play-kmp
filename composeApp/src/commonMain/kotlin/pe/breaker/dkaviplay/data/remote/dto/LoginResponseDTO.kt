package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO (
    val token: String,
    val firebaseToken: String,
    val cripKey: String
)