package pe.breaker.dkaviplay.data.remote.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReservaDTO(

    @SerialName("id")
    val id: Long,

    @SerialName("id_sede")
    val idSede: Long? = null,

    @SerialName("id_mesa")
    val idMesa: Long? = null,

    @SerialName("tipo_juego")
    val tipoJuego: String = "BILLAR",

    @SerialName("fecha_inicio")
    val fechaInicio: String? = null,

    @SerialName("fecha_fin")
    val fechaFin: String? = null,

    @SerialName("uuid_user1")
    val uuidUser1: String? = null,

    @SerialName("user1_ready")
    val user1Ready: Boolean = false,

    @SerialName("uuid_user2")
    val uuidUser2: String? = null,

    @SerialName("user2_ready")
    val user2Ready: Boolean = false,

    @SerialName("uuid_user_pendiente")
    val uuidUserPendiente: String? = null,

    @SerialName("id_estado")
    val idEstado: Long? = null,

    @SerialName("esperando_confirmacion")
    val esperandoConfirmacion: Boolean = false,

    @SerialName("is_movil")
    val isMovil: Boolean = true,

    @SerialName("reserva_simple")
    val reservaSimple: Boolean = false,

    @SerialName("status")
    val status: Boolean = true,

    @SerialName("monto_total_monedas")
    val montoTotalMonedas: Double = 0.0,

    @SerialName("user_cr")
    val userCr: String? = null,

    @SerialName("user_up")
    val userUp: String? = null,

    @SerialName("date_cr")
    val dateCr: String? = null,

    @SerialName("date_up")
    val dateUp: String? = null
)