package pe.breaker.dkaviplay.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestVerifyCodeDTO(
    val userId: Int?,
    val code: String?
)