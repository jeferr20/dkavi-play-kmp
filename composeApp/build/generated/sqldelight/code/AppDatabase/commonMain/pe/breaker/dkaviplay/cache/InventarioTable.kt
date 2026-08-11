package pe.breaker.dkaviplay.cache

import kotlin.Long

public data class InventarioTable(
  public val id: Long,
  public val usuarioUid: Long,
  public val itemId: Long,
  public val cantidad: Long,
)
