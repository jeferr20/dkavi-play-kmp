package pe.breaker.dkaviplay.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class InventarioEntity(
    val usuarioUid: Int,
    val articuloId: Int,
    val cantidad: Int
)