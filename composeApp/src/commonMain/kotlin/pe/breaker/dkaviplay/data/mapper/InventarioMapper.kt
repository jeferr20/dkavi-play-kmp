package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.cache.InventarioTable
import pe.breaker.dkaviplay.data.entity.InventarioEntity
import pe.breaker.dkaviplay.data.remote.supabase.InventarioDTO

fun InventarioEntity.toTable(): InventarioTable {
    return InventarioTable(
        id = 0L,
        usuarioUid = this.usuarioUid.toLong(),
        itemId = this.articuloId.toLong(),
        cantidad = this.cantidad.toLong()
    )
}

fun InventarioDTO.toEntity(): InventarioEntity {
    return InventarioEntity(
        usuarioUid = this.userMovilId.toInt(),
        articuloId = this.articulo.toInt(),
        cantidad = this.cantidad
    )
}