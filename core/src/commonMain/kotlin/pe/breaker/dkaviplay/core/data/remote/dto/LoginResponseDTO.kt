package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO (
    val token: String,
    val firebaseToken: String? = null,
    val cripKey: String
)