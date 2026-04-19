package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class InventarioFirebase(
    val id: Int,
    val cantidad: Int
)