package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.cache.UsuarioTable
import pe.breaker.dkaviplay.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.data.remote.dto.UserMovilDto

fun UserMovilDto.toEntity() = UsuarioEntity(
    usuarioUid = id.toInt(),
    usuario = usuario ?: "",
    puntos = puntos?.toInt() ?: 0,
    partidasGanadas = partidasGanadas ?: 0,
    partidasJugadas = partidasJugadas ?: 0,
    urlImagen = urlImagen,
    rol = "Movil",
    genero = genero?: "",
    departamento = departamento?: "",
    distrito = distrito?: "",
    provincia = provincia?: "",
    sedePreferencia = sedePreferencia?: "",
    monedas = monedas?.toInt() ?: 0,
    uidAuth = uuidAuth ?: "",
    nombres = nombres?: "",
    apellidoPaterno = apellidoPaterno?: "",
    apellidoMaterno = apellidoMaterno?: "",
    celular = celular ?: "",
    correo = correo ?: ""
)

fun UsuarioEntity.toTable() = UsuarioTable(
    usuarioUid = usuarioUid.toString(),
    usuario = usuario,
    puntos = puntos.toLong(),
    partidasGanadas = partidasGanadas.toLong(),
    partidasJugadas = partidasJugadas.toLong(),
    urlImagen = urlImagen,
    rol = rol,
    genero = genero,
    departamento = departamento,
    distrito = distrito,
    provincia = provincia,
    sedePreferencia = sedePreferencia,
    monedas = monedas.toLong(),
    uidAuth = uidAuth,
    nombres = nombres,
    apellidoPaterno = apellidoPaterno,
    apellidoMaterno = apellidoMaterno,
    celular = celular,
    correo = correo
)