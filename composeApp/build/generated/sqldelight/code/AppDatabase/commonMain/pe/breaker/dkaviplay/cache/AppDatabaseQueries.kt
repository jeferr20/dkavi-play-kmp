package pe.breaker.dkaviplay.cache

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class AppDatabaseQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getUsuarioTable(mapper: (
    usuarioUid: String,
    uidAuth: String,
    usuario: String,
    puntos: Long,
    partidasGanadas: Long,
    partidasJugadas: Long,
    urlImagen: String?,
    rol: String,
    genero: String,
    departamento: String,
    distrito: String,
    provincia: String,
    sedePreferencia: String,
    monedas: Long,
    nombres: String,
    apellidoPaterno: String,
    apellidoMaterno: String,
    celular: String,
    correo: String,
  ) -> T): Query<T> = Query(-141_906_260, arrayOf("UsuarioTable"), driver, "AppDatabase.sq",
      "getUsuarioTable",
      "SELECT UsuarioTable.usuarioUid, UsuarioTable.uidAuth, UsuarioTable.usuario, UsuarioTable.puntos, UsuarioTable.partidasGanadas, UsuarioTable.partidasJugadas, UsuarioTable.urlImagen, UsuarioTable.rol, UsuarioTable.genero, UsuarioTable.departamento, UsuarioTable.distrito, UsuarioTable.provincia, UsuarioTable.sedePreferencia, UsuarioTable.monedas, UsuarioTable.nombres, UsuarioTable.apellidoPaterno, UsuarioTable.apellidoMaterno, UsuarioTable.celular, UsuarioTable.correo FROM UsuarioTable LIMIT 1") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!,
      cursor.getLong(4)!!,
      cursor.getLong(5)!!,
      cursor.getString(6),
      cursor.getString(7)!!,
      cursor.getString(8)!!,
      cursor.getString(9)!!,
      cursor.getString(10)!!,
      cursor.getString(11)!!,
      cursor.getString(12)!!,
      cursor.getLong(13)!!,
      cursor.getString(14)!!,
      cursor.getString(15)!!,
      cursor.getString(16)!!,
      cursor.getString(17)!!,
      cursor.getString(18)!!
    )
  }

  public fun getUsuarioTable(): Query<UsuarioTable> = getUsuarioTable { usuarioUid, uidAuth,
      usuario, puntos, partidasGanadas, partidasJugadas, urlImagen, rol, genero, departamento,
      distrito, provincia, sedePreferencia, monedas, nombres, apellidoPaterno, apellidoMaterno,
      celular, correo ->
    UsuarioTable(
      usuarioUid,
      uidAuth,
      usuario,
      puntos,
      partidasGanadas,
      partidasJugadas,
      urlImagen,
      rol,
      genero,
      departamento,
      distrito,
      provincia,
      sedePreferencia,
      monedas,
      nombres,
      apellidoPaterno,
      apellidoMaterno,
      celular,
      correo
    )
  }

  public fun <T : Any> getInventarioTable(mapper: (
    id: Long,
    usuarioUid: Long,
    itemId: Long,
    cantidad: Long,
  ) -> T): Query<T> = Query(428_934_641, arrayOf("InventarioTable"), driver, "AppDatabase.sq",
      "getInventarioTable",
      "SELECT InventarioTable.id, InventarioTable.usuarioUid, InventarioTable.itemId, InventarioTable.cantidad FROM InventarioTable") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getLong(2)!!,
      cursor.getLong(3)!!
    )
  }

  public fun getInventarioTable(): Query<InventarioTable> = getInventarioTable { id, usuarioUid,
      itemId, cantidad ->
    InventarioTable(
      id,
      usuarioUid,
      itemId,
      cantidad
    )
  }

  public fun <T : Any> getHorarioTable(mapper: (
    id: Long,
    usuarioUid: Long,
    dia: Long,
    horaInicio: String,
    horaFin: String,
    habilitado: Long,
  ) -> T): Query<T> = Query(1_662_397_048, arrayOf("HorarioTable"), driver, "AppDatabase.sq",
      "getHorarioTable",
      "SELECT HorarioTable.id, HorarioTable.usuarioUid, HorarioTable.dia, HorarioTable.horaInicio, HorarioTable.horaFin, HorarioTable.habilitado FROM HorarioTable") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getLong(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getLong(5)!!
    )
  }

  public fun getHorarioTable(): Query<HorarioTable> = getHorarioTable { id, usuarioUid, dia,
      horaInicio, horaFin, habilitado ->
    HorarioTable(
      id,
      usuarioUid,
      dia,
      horaInicio,
      horaFin,
      habilitado
    )
  }

  public fun insertUsuarioTable(UsuarioTable: UsuarioTable) {
    driver.execute(1_999_834_083, """
        |INSERT OR REPLACE INTO UsuarioTable (usuarioUid, uidAuth, usuario, puntos, partidasGanadas, partidasJugadas, urlImagen, rol, genero, departamento, distrito, provincia, sedePreferencia, monedas, nombres, apellidoPaterno, apellidoMaterno, celular, correo)
        |VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 19) {
          bindString(0, UsuarioTable.usuarioUid)
          bindString(1, UsuarioTable.uidAuth)
          bindString(2, UsuarioTable.usuario)
          bindLong(3, UsuarioTable.puntos)
          bindLong(4, UsuarioTable.partidasGanadas)
          bindLong(5, UsuarioTable.partidasJugadas)
          bindString(6, UsuarioTable.urlImagen)
          bindString(7, UsuarioTable.rol)
          bindString(8, UsuarioTable.genero)
          bindString(9, UsuarioTable.departamento)
          bindString(10, UsuarioTable.distrito)
          bindString(11, UsuarioTable.provincia)
          bindString(12, UsuarioTable.sedePreferencia)
          bindLong(13, UsuarioTable.monedas)
          bindString(14, UsuarioTable.nombres)
          bindString(15, UsuarioTable.apellidoPaterno)
          bindString(16, UsuarioTable.apellidoMaterno)
          bindString(17, UsuarioTable.celular)
          bindString(18, UsuarioTable.correo)
        }
    notifyQueries(1_999_834_083) { emit ->
      emit("UsuarioTable")
    }
  }

  public fun insertInventarioTable(InventarioTable: InventarioTable) {
    driver.execute(-1_018_656_422, """
        |INSERT OR REPLACE INTO InventarioTable (id, usuarioUid, itemId, cantidad)
        |VALUES (?, ?, ?, ?)
        """.trimMargin(), 4) {
          bindLong(0, InventarioTable.id)
          bindLong(1, InventarioTable.usuarioUid)
          bindLong(2, InventarioTable.itemId)
          bindLong(3, InventarioTable.cantidad)
        }
    notifyQueries(-1_018_656_422) { emit ->
      emit("InventarioTable")
    }
  }

  public fun insertHorarioTable(HorarioTable: HorarioTable) {
    driver.execute(-490_829_905, """
        |INSERT OR REPLACE INTO HorarioTable (id, usuarioUid, dia, horaInicio, horaFin, habilitado)
        |VALUES (?, ?, ?, ?, ?, ?)
        """.trimMargin(), 6) {
          bindLong(0, HorarioTable.id)
          bindLong(1, HorarioTable.usuarioUid)
          bindLong(2, HorarioTable.dia)
          bindString(3, HorarioTable.horaInicio)
          bindString(4, HorarioTable.horaFin)
          bindLong(5, HorarioTable.habilitado)
        }
    notifyQueries(-490_829_905) { emit ->
      emit("HorarioTable")
    }
  }

  public fun removeUsuarioTable() {
    driver.execute(1_363_929_390, """DELETE FROM UsuarioTable""", 0)
    notifyQueries(1_363_929_390) { emit ->
      emit("UsuarioTable")
    }
  }

  public fun removeInventarioTable() {
    driver.execute(-154_622_929, """DELETE FROM InventarioTable""", 0)
    notifyQueries(-154_622_929) { emit ->
      emit("InventarioTable")
    }
  }

  public fun removeHorarioTable() {
    driver.execute(-1_126_734_598, """DELETE FROM HorarioTable""", 0)
    notifyQueries(-1_126_734_598) { emit ->
      emit("HorarioTable")
    }
  }
}
