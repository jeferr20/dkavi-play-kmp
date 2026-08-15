package pe.breaker.dkaviplay.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class InventarioEntity(
    val id: Long,
    val usuarioUid: Long,
    val articuloId: Long,
    val cantidad: Int
)