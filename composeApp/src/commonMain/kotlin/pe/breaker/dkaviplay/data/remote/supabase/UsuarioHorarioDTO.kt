package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioHorarioDTO(

    @SerialName("id")
    val id: Long,

    @SerialName("usermovil_id")
    val userMovilId: Long? = null,

    @SerialName("dia")
    val dia: Int? = null,

    @SerialName("horaInicio")
    val horaInicio: String? = null,

    @SerialName("horaFin")
    val horaFin: String? = null,

    @SerialName("habilitado")
    val habilitado: Boolean = true
)