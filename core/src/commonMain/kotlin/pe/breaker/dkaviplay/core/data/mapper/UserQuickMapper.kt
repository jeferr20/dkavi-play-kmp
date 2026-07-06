package pe.breaker.dkaviplay.core.data.mapper

import pe.breaker.dkaviplay.core.data.remote.firebase.UserHorarioFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.UserMovilFirebase
import pe.breaker.dkaviplay.core.domain.model.Horario
import pe.breaker.dkaviplay.core.domain.model.UserQuick
import pe.breaker.dkaviplay.core.domain.model.rango.RangoRegistry

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