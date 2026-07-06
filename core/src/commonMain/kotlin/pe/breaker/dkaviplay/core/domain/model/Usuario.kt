package pe.breaker.dkaviplay.core.domain.model

data class Usuario(
    val uid: String,
    val usuario: String,
    val puntos: Int,
    val urlImagen: String?,
    val horarios: List<Horario>,
    val inventario: List<ItemInventario>
)