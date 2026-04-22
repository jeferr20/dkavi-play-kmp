package pe.breaker.dkaviplay.domain.model.rango

import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio

data class Rango(
    val nivel: Int,
    val categoria: String,
    val puntosMin: Int,
    val puntosMax: Int?,
    val recompensas: List<DetallePremio>
)