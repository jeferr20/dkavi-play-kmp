package pe.breaker.dkaviplay.data.mapper

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.data.remote.firebase.UserMovilFirebase
import pe.breaker.dkaviplay.domain.model.rango.RangoRegistry
import pe.breaker.dkaviplay.cache.UsuarioTable

fun UserMovilFirebase.toEntity() = UsuarioEntity(
    usuarioUid = userUid ?: "",
    usuario = user,
    rango = RangoRegistry.obtenerRangoPorPuntos(puntos).categoria,
    puntos = puntos,
    partidasGanadas = partidasGanadas,
    partidasJugadas = partidasJugadas,
    urlImagen = urlImagen,
    rol = rol,
    inventarioJson = Json.encodeToString(this.inventario),
    genero = genero ?: "",
    departamento = departamento ?: "",
    distrito = distrito ?: "",
    provincia = provincia ?: "",
    sedePreferencia = sedePreferencia ?: "",
    horariosJson = Json.encodeToString(this.horarios ?: emptyList())
)

fun UsuarioEntity.toTable() = UsuarioTable(
    usuarioUid = usuarioUid,
    usuario = usuario,
    rango = rango,
    puntos = puntos.toLong(),
    partidasGanadas = partidasGanadas.toLong(),
    partidasJugadas = partidasJugadas.toLong(),
    urlImagen = urlImagen,
    rol = rol,
    inventarioJson = inventarioJson,
    genero = genero,
    departamento = departamento,
    distrito = distrito,
    provincia = provincia,
    sedePreferencia = sedePreferencia,
    horariosJson = horariosJson
)