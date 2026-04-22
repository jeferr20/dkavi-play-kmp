package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class MesaFirebase(
    val descripcion: String?,
    val uuidSede: String?
)