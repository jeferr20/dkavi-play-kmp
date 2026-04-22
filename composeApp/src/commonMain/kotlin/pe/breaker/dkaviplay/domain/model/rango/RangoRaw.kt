package pe.breaker.dkaviplay.domain.model.rango

data class RangoRaw(
    val nivel: Int,
    val categoria: String,
    val puntosMin: Int,
    val puntosMax: Int?,
    val recompensasIds: List<Int>
)