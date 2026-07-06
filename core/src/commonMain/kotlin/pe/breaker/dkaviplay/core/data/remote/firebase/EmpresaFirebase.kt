package pe.breaker.dkaviplay.core.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class EmpresaFirebase(
    val emailOwner: String?,
    val logo: String?,
    val nombre: String?,
    val owner: String?,
    val ruc: String?,
)