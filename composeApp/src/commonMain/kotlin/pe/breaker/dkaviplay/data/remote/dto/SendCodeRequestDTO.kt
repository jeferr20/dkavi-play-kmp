package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendCodeRequestDTO (
    val emailUser: String?,
    val phoneUser: String?
)