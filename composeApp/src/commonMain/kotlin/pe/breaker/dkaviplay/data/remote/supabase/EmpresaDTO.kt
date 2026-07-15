package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmpresaDTO(

    @SerialName("id")
    val id: Long,

    @SerialName("nombre")
    val nombre: String? = null,

    @SerialName("owner")
    val owner: String? = null,

    @SerialName("email_owner")
    val emailOwner: String? = null,

    @SerialName("logo")
    val logo: String? = null,

    @SerialName("ruc")
    val ruc: String? = null,

    @SerialName("status")
    val status: Boolean = true,

    @SerialName("created_at")
    val createdAt: String? = null
)