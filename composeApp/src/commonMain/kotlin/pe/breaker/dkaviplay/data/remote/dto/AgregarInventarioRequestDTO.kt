package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AgregarInventarioRequestDTO(
    val userUid: String?,
    val listaInventario: List<ItemInventarioRequestDTO>?,
    val reservaId: String?,
    val isCreador: Boolean?
)

@Serializable
data class ItemInventarioRequestDTO(
    val id: Int,
    val cantidad: Int
)