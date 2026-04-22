package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequestDTO(
    val id: String?,
    val newPassword: String?
)