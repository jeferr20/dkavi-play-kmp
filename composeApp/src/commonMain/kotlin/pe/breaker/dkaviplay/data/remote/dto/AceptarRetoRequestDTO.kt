package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AceptarRetoRequestDTO(
    val estadoId: Int?,
    val idReserva: String?,
    val mesaDescripcion: String?,
    val idSede: String?
)