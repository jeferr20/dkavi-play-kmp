package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CerrarJuegoMutuoRequestDTO(
    val reservaId: String,
    val juegoId: String
)