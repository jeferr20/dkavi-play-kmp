package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MesaDTO(

    @SerialName("id")
    val id: Long,

    @SerialName("id_sede")
    val idSede: Long,

    @SerialName("descripcion")
    val descripcion: String,

    @SerialName("status")
    val status: Boolean? = null,

    @SerialName("user_cr")
    val userCr: String? = null,

    @SerialName("date_cr")
    val dateCr: String? = null,

    @SerialName("user_up")
    val userUp: String? = null,

    @SerialName("date_up")
    val dateUp: String? = null
)