package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CerrarJuegoMutuoRequestDTO(
    val reservaId: Int,
    val juegoId: Int
)