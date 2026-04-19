package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegistrarJuegoRequestDTO(
    val uuidUser1: String?,
    val uuidUser2: String?,
    val user1: String?,
    val user2: String?,
    val tipoJuego: String?,
    val tipoValidacion: String?,
    val partidas: List<ResultadosPartidasDTO>,
    val uuidArbitro: String?,
    val arbitro: String?,
    val reservaId: String?,
    val contrincanteUid: String?,
    val sender: String?
)