package pe.breaker.dkaviplay.cache

import kotlin.Long
import kotlin.String

public data class UsuarioTable(
  public val usuarioUid: String,
  public val uidAuth: String,
  public val usuario: String,
  public val puntos: Long,
  public val partidasGanadas: Long,
  public val partidasJugadas: Long,
  public val urlImagen: String?,
  public val rol: String,
  public val genero: String,
  public val departamento: String,
  public val distrito: String,
  public val provincia: String,
  public val sedePreferencia: String,
  public val monedas: Long,
  public val nombres: String,
  public val apellidoPaterno: String,
  public val apellidoMaterno: String,
  public val celular: String,
  public val correo: String,
)
