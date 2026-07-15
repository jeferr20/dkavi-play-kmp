package pe.breaker.dkaviplay.data.remote.supabase.view

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMovilView (
    @SerialName("user_uuid_auth") val userUid: String,
    @SerialName("usuario") val usuario: String,
    @SerialName("imagen") val imagen: String?,
)