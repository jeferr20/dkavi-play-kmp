package pe.breaker.dkaviplay.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TorneoResponseDTO(
    val torneoUid: String? = null,
    val sedeUid: String? = null,
    val nombreTorneo: String? = null,
    val bannerTorneo: String? = null,
    val sedeTorneo: String? = null,
    val descripcionTorneo: String? = null,
    val priceInscripcion: Double? = null,
    val price1: Double? = null,
    val price2: Double? = null,
    val price3: Double? = null,
    val miembrosMaximo: Int? = null,
    val miembrosActuales: Int? = null,
    // Recibimos los objetos de timestamp de Firebase
    val fechaInicio: TimestampResponseDTO? = null,
    val fechaMaximaInscripcion: TimestampResponseDTO? = null,
    val fechaAperturaInscripcion: TimestampResponseDTO? = null,
    val inscrito: Boolean? = null
)