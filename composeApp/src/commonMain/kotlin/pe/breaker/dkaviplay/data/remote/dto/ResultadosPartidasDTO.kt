package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResultadosPartidasDTO(
    @SerialName("numero")
    val numero: Int,

    @SerialName("ganadorId")
    val ganadorId: String
)