package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class SedeHorarioFirebase(
    val activo: Boolean?,
    val dia: String?,
    val fin: String?,
    val inicio: String?
)