package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResultadosPartidasDTO(
    val numero: Int,
    val ganadorId: String
)