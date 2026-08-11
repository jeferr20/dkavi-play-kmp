package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActualizarPuntosRequestDTO(
    val juegoId: Int,
    val partidas: List<ResultadosPartidasDTO>,
    val tipoJuego: String?,
    val reservaId: Int?,
    val contrincanteUid: String,
    val sender: String?
)