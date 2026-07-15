package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequestDTO(
    val id: Int?,
    val newPassword: String?
)