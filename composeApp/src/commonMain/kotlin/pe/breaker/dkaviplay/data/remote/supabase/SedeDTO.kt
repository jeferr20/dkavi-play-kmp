package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SedeDTO(
    @SerialName("id")
    val id: Long,

    @SerialName("id_empresa")
    val idEmpresa: Long? = null,

    @SerialName("nombre")
    val nombre: String? = null,

    @SerialName("numero_sede")
    val numeroSede: String? = null,

    @SerialName("direccion")
    val direccion: String? = null,

    @SerialName("referencia")
    val referencia: String? = null,

    @SerialName("departamento")
    val departamento: String? = null,

    @SerialName("provincia")
    val provincia: String? = null,

    @SerialName("distrito")
    val distrito: String? = null,

    @SerialName("latitud")
    val latitud: String? = null,

    @SerialName("longitud")
    val longitud: String? = null,

    @SerialName("imagen")
    val imagen: String? = null,

    @SerialName("horario")
    val horario: List<HorarioSedeDTO> = emptyList(),

    @SerialName("status")
    val status: Boolean = true,

    @SerialName("user_cr")
    val userCr: String? = null,

    @SerialName("user_up")
    val userUp: String? = null,

    @SerialName("date_cr")
    val dateCr: String? = null,

    @SerialName("date_up")
    val dateUp: String? = null
)