package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(
    val usuario: String,
    val password: String
)