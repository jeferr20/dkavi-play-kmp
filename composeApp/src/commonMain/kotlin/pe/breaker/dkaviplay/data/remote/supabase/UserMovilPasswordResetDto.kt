package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMovilPasswordResetDto(

    @SerialName("id")
    val id: Int? = null,

    @SerialName("usermovil_id")
    val userMovilId: Int? = null,

    @SerialName("codigo")
    val codigo: String? = null,

    @SerialName("expira_at")
    val expiraAt: String? = null,

    @SerialName("intentos")
    val intentos: Int? = null,

    @SerialName("usado")
    val usado: Boolean = false,

    @SerialName("date_cr")
    val dateCr: String? = null,

    @SerialName("date_up")
    val dateUp: String? = null
)