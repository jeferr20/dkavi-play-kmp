package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePasswordRequestDTO(
    val id: String?,
    val newPassword: String?
)