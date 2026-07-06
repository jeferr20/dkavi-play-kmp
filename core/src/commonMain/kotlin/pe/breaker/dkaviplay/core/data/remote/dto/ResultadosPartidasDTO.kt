package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResultadosPartidasDTO(
    val numero: Int,
    val ganadorId: String
)