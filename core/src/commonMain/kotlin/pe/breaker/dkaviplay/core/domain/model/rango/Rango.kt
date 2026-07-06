package pe.breaker.dkaviplay.core.domain.model.rango

import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio

data class Rango(
    val nivel: Int,
    val categoria: String,
    val puntosMin: Int,
    val puntosMax: Int?,
    val recompensas: List<DetallePremio>
)