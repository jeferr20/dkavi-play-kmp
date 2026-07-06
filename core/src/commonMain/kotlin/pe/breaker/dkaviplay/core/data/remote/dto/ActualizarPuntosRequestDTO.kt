package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActualizarPuntosRequestDTO(
    val juegoId: String,
    val partidas: List<ResultadosPartidasDTO>,
    val tipoJuego: String?,
    val reservaId: String?,
    val contrincanteUid: String,
    val sender: String?
)