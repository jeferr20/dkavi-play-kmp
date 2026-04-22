package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.data.remote.firebase.UserHorarioFirebase
import pe.breaker.dkaviplay.data.remote.firebase.UserMovilFirebase
import pe.breaker.dkaviplay.domain.model.Horario
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.model.rango.RangoRegistry

fun UserMovilFirebase.toDomain() = UserQuick(
    userUid = userUid,
    usuario = user,
    imagen = urlImagen,
    rango = RangoRegistry.obtenerRangoPorPuntos(puntos).categoria,
    horarios = horarios?.map { it.toDomain() },
    partidasGanadas = partidasGanadas,
    partidasJugadas = partidasJugadas,
    puntos = puntos
)

fun UserHorarioFirebase.toDomain() = Horario(
    nombre = nombre ?: "",
    habilitado = habilitado ?: false,
    horaInicio = horaInicio ?: "",
    horaFin = horaFin ?: ""
)