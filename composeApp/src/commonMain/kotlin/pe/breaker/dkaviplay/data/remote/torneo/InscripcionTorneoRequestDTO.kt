package pe.breaker.dkaviplay.data.remote.torneo

import kotlinx.serialization.Serializable

@Serializable
data class InscripcionTorneoRequestDTO(val torneoUid: String)