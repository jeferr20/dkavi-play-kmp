package pe.breaker.dkaviplay.core.data.mapper

import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.cache.UsuarioTable
import pe.breaker.dkaviplay.core.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.core.data.remote.firebase.InventarioFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.UserHorarioFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.UserMovilFirebase
import pe.breaker.dkaviplay.core.domain.model.Horario
import pe.breaker.dkaviplay.core.domain.model.ItemInventario
import pe.breaker.dkaviplay.core.domain.model.Usuario
import pe.breaker.dkaviplay.core.domain.model.rango.RangoRegistry

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

fun UsuarioTable.toDomain(): Usuario {
    val itemsInventario = try {
        Json.decodeFromString<List<InventarioFirebase>>(this.inventarioJson.orEmpty()).map {
            ItemInventario(id = it.id, cantidad = it.cantidad)
        }
    } catch (e: Exception) {
        emptyList()
    }

    val horarios = try {
        Json.decodeFromString<List<UserHorarioFirebase>>(this.horariosJson).map {
            Horario(
                nombre = it.nombre ?: "",
                habilitado = it.habilitado ?: false,
                horaInicio = it.horaInicio ?: "",
                horaFin = it.horaFin ?: ""
            )
        }
    } catch (e: Exception) {
        emptyList()
    }

    return Usuario(
        uid = this.usuarioUid,
        usuario = this.usuario,
        puntos = this.puntos.toInt(),
        urlImagen = this.urlImagen,
        inventario = itemsInventario,
        horarios = horarios
    )
}