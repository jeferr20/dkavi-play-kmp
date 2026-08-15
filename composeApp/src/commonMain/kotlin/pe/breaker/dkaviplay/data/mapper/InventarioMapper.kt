package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.cache.InventarioTable
import pe.breaker.dkaviplay.data.entity.InventarioEntity
import pe.breaker.dkaviplay.data.remote.supabase.InventarioDTO

fun InventarioEntity.toTable(): InventarioTable {
    return InventarioTable(
        id = this.id,
        usuarioUid = this.usuarioUid,
        itemId = this.articuloId,
        cantidad = this.cantidad.toLong()
    )
}

fun InventarioDTO.toEntity(): InventarioEntity {
    return InventarioEntity(
        id = this.id,
        usuarioUid = this.userMovilId,
        articuloId = this.articulo,
        cantidad = this.cantidad
    )
}