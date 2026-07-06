package pe.breaker.dkaviplay.core.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class MesaFirebase(
    val descripcion: String?,
    val uuidSede: String?
)