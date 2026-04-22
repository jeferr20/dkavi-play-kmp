package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(
    val usuario: String,
    val password: String
)