package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendCodeRequestDTO (
    val emailUser: String?,
    val phoneUser: String?
)