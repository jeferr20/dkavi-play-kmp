package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.data.remote.firebase.UserMovilFirebase
import pe.breaker.dkaviplay.data.remote.supabase.rpc.UserQuickDTO
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.model.rango.RangoRegistry

fun UserMovilFirebase.toDomain() = UserQuick(
    userUid = userUid,
    usuario = user,
    imagen = urlImagen,
    rango = RangoRegistry.obtenerRangoPorPuntos(puntos).categoria,
    partidasGanadas = partidasGanadas,
    partidasJugadas = partidasJugadas,
    puntos = puntos
)

fun UserQuickDTO.toDomain() = UserQuick(
    userUid = uuidAuth,
    usuario = usuario,
    imagen = imagen,
    rango = RangoRegistry.obtenerRangoPorPuntos(puntos).categoria,
    partidasGanadas = partidasGanadas,
    partidasJugadas = partidasJugadas,
    puntos = puntos
)