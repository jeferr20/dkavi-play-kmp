package pe.breaker.dkaviplay.data.remote.supabase.view

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MesaSedeView(
    @SerialName("mesa_id") val mesaId: Int,
    @SerialName("mesa_nombre") val mesaNombre: String,
    @SerialName("sede_id") val sedeId: Int,
    @SerialName("sede_nombre") val sedeNombre: String,
    @SerialName("sede_logo") val sedeLogo: String,
)