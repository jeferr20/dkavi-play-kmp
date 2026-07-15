package pe.breaker.dkaviplay.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestUpdateTokenDTO(
    val authUid: String,
    val token: String?
)