package pe.breaker.dkaviplay.cache

public data class HorarioTable(
  public val id: Long,
  public val usuarioUid: Long,
  public val dia: Long,
  public val horaInicio: String,
  public val horaFin: String,
  public val habilitado: Long,
)
