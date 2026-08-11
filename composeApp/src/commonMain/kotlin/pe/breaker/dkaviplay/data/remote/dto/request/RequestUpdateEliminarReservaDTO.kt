package pe.breaker.dkaviplay.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestUpdateEliminarReservaDTO(
    val idReserva: Int,
    val estadoId: Int
)