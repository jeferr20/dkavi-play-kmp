package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InventarioDTO(
    @SerialName("id") val id: Long,
    @SerialName("usermovil_id") val userMovilId: Long,
    @SerialName("articulo_id") val articulo: Long,
    @SerialName("cantidad") val cantidad: Int
)