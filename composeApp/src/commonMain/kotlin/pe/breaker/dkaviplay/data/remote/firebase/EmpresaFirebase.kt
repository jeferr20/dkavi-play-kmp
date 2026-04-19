package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class EmpresaFirebase(
    val emailOwner: String?,
    val logo: String?,
    val nombre: String?,
    val owner: String?,
    val ruc: String?,
)