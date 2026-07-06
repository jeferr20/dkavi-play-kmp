package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AceptarRetoRequestDTO(
    val estadoId: Int?,
    val idReserva: String?,
    val mesaDescripcion: String?,
    val idSede: String?
)