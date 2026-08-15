package pe.breaker.dkaviplay.data.remote.supabase.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReservaJuegoDTO(
    @SerialName("mesa")
    val mesa: String,

    @SerialName("sede")
    val sede: String,

    @SerialName("retado")
    val retado: String,

    @SerialName("creador")
    val creador: String,

    @SerialName("sede_id")
    val sedeId: Int,

    @SerialName("juego_id")
    val juegoId: Int? = null,

    @SerialName("partidasList")
    val partidas: String? = null,

    @SerialName("estado_id")
    val estadoId: Int,

    @SerialName("fecha_fin")
    val fechaFin: String? = null,

    @SerialName("sede_logo")
    val sedeLogo: String,

    @SerialName("reserva_id")
    val reservaId: Int,

    @SerialName("retado_uid")
    val retadoUid: String,

    @SerialName("tipo_juego")
    val tipoJuego: String,

    @SerialName("creador_uid")
    val creadorUid: String,

    @SerialName("ganador_uid")
    val ganadorUid: String? = null,

    @SerialName("monto_total")
    val montoTotal: Double,

    @SerialName("fecha_inicio")
    val fechaInicio: String,

    @SerialName("user_creador_ready")
    val userCreadorReady: Boolean,

    @SerialName("user_pendiente_uid")
    val userPendienteUid: String,

    @SerialName("user_retado_ready")
    val userRetadoReady: Boolean,

    @SerialName("esperando_confirmacion")
    val esperandoConfirmacion: Boolean
)