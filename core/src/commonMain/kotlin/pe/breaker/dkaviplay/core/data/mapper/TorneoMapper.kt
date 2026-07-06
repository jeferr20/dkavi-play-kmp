package pe.breaker.dkaviplay.core.data.mapper

import pe.breaker.dkaviplay.core.util.timestampToStringCompleto
import pe.breaker.dkaviplay.data.remote.TorneoResponseDTO
import pe.breaker.dkaviplay.core.domain.model.Torneo

fun TorneoResponseDTO.toDomain(): Torneo {
    return Torneo(
        torneoUid = torneoUid ?: "",
        sedeUid = sedeUid ?: "",
        nombreTorneo = nombreTorneo ?: "Sin nombre",
        bannerTorneo = bannerTorneo ?: "",
        sedeTorneo = sedeTorneo ?: "",
        descripcionTorneo = descripcionTorneo ?: "",
        priceInscripcion = priceInscripcion ?: 0.0,
        price1 = price1 ?: 0.0,
        price2 = price2 ?: 0.0,
        price3 = price3 ?: 0.0,
        miembrosMaximo = miembrosMaximo ?: 0,
        miembrosActuales = miembrosActuales ?: 0,
        fechaInicio = fechaInicio?.let { timestampToStringCompleto(it._seconds) } ?: "",
        fechaMaximaInscripcion = fechaMaximaInscripcion?.let { timestampToStringCompleto(it._seconds) } ?: "",
        fechaAperturaInscripcion = fechaAperturaInscripcion?.let { timestampToStringCompleto(it._seconds) } ?: "",
        inscrito = inscrito ?: false

    )
}