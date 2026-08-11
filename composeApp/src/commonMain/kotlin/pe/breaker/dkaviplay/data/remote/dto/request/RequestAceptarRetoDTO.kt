package pe.breaker.dkaviplay.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestAceptarRetoDTO(
    val idReserva: Int,
    val estadoId: Int,
    val montoTotal: Double,
    val user1: String
)