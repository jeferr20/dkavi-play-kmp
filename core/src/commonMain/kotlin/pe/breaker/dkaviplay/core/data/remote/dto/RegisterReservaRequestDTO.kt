package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReservaRequestDTO(
    val sedeUid: String,
    val fechaHoraInicio: pe.breaker.dkaviplay.core.data.remote.dto.TimestampDTO,
    val fechaHoraFin: pe.breaker.dkaviplay.core.data.remote.dto.TimestampDTO,
    val montoTotal: Double,
    val uuidUser1: String,
    val user1: String,
    val uuidUser2: String,
    val user2: String,
    val tipoJuego: String,
    val mesaUid: String?,
)