package pe.breaker.dkaviplay.core.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class InventarioFirebase(
    val id: Int,
    val cantidad: Int
)