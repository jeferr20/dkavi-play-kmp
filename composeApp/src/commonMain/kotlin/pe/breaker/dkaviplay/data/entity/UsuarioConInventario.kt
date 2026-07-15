package pe.breaker.dkaviplay.data.entity

data class UsuarioConInventario(
    val usuario: UsuarioEntity,
    val inventario: List<InventarioEntity>,
    val horario: List<HorarioEntity>
)