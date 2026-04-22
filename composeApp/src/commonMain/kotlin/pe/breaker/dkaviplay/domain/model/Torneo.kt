package pe.breaker.dkaviplay.domain.model

data class Torneo(
    val torneoUid: String,
    val sedeUid: String,
    val nombreTorneo: String,
    val bannerTorneo: String,
    val sedeTorneo: String,
    val descripcionTorneo: String,
    val priceInscripcion: Double,
    val price1: Double,
    val price2: Double,
    val price3: Double,
    val miembrosMaximo: Int,
    val miembrosActuales: Int,
    val fechaInicio: String,
    val fechaMaximaInscripcion: String,
    val fechaAperturaInscripcion: String,
    val inscrito: Boolean,
)
