package pe.breaker.dkaviplay.core.data.mapper

import pe.breaker.dkaviplay.core.data.remote.firebase.JuegoFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.MesaFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.ReservaFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.SedeFirebase
import pe.breaker.dkaviplay.core.util.timestampToStringCompleto
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.core.domain.model.ReservaEstado

fun mapToReserva(
    id: String,
    sede: SedeFirebase,
    reserva: ReservaFirebase,
    mesa: MesaFirebase,
    juego: JuegoFirebase?
): Reserva {
    val estadoEnum = ReservaEstado.fromId(reserva.uuidEstado)
    return Reserva(
        reservaUid = id,
        estado = estadoEnum.descripcion,
        estadoInt = estadoEnum.id,
        sedeImagen = sede.imagen ?: "",
        sede = sede.nombre ?: "",
        sedeUid = reserva.uuidSede ?: "",
        tipoJuego = reserva.tipoJuego ?: "",
        fechaInicio = reserva.fechaInicio?.let { timestampToStringCompleto(it.seconds) }
            ?: "Sin fecha",
        fechaFin = reserva.fechaFin?.let { timestampToStringCompleto(it.seconds) } ?: "Sin hora",
        estadoColor = estadoEnum.colorHex,
        montoTotal = reserva.montoTotal ?: 0.0,
        montoPagado = reserva.montoTotalPagado ?: 0.0,
        creador = reserva.user1 ?: "",
        creadorUid = reserva.uuidUser1 ?: "",
        retado = reserva.user2 ?: "",
        retadoUid = reserva.uuidUser2 ?: "",
        mesa = mesa.descripcion ?: "",
        juegoUid = juego?.uuidJuego ?: "",
        userPendienteUid = reserva.uuidUserPendiente ?: "",
        esperandoConfirmacion = reserva.esperandoConfirmacion ?: false,
        partidas = juego?.partidas ?: emptyList(),
        ganadorUid = juego?.uuidGanador ?: "",
        userCreadorReady = reserva.user1Ready ?: false,
        userRetadoReady = reserva.user2Ready ?: false
    )
}