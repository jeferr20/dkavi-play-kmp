package pe.breaker.dkaviplay.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestHorarioDTO(
    val habilitado: Boolean,
    val horaInicio: String,
    val horaFin: String,
    val dia: Int
)