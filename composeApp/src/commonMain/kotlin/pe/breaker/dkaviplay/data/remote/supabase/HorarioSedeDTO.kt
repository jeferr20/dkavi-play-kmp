package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.Serializable

@Serializable
data class HorarioSedeDTO(
    val dia: String,
    val inicio: String,
    val fin: String,
    val activo: Boolean
)