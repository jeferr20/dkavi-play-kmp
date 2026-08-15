package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReservaRequestDTO(
    val sedeUid: Int,
    val fechaHoraInicio: String,
    val fechaHoraFin: String? = null,
    val montoTotal: Double,
    val user1: String,
    val uuidUser2: String,
    val user2: String,
    val tipoJuego: String,
    val mesaUid: String?,
)