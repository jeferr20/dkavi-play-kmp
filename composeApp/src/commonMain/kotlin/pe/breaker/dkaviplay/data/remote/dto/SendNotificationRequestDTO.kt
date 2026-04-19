package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendNotificationRequestDTO(
    val uid: String,
    val title: String,
    val message: String,
    val data: DataNotificacionDTO
)

@Serializable
data class DataNotificacionDTO(
    val action: String
)