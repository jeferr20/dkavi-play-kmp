package pe.breaker.dkaviplay.data.remote.supabase.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReservaRpcDTO(
    @SerialName("reserva_id")
    val reservaId: Int,

    @SerialName("estado")
    val estado: String,

    @SerialName("estado_color")
    val estadoColor: String,

    @SerialName("estado_id")
    val estadoId: Int,

    @SerialName("sede_imagen")
    val sedeImagen: String? = null,

    @SerialName("sede")
    val sede: String,

    @SerialName("sede_id")
    val sedeId: Int,

    @SerialName("tipo_juego")
    val tipoJuego: String,

    @SerialName("fecha_inicio")
    val fechaInicio: String,

    @SerialName("fecha_fin")
    val fechaFin: String,

    @SerialName("monto_total")
    val montoTotal: Double = 0.0,

    @SerialName("mesa")
    val mesa: String,

    @SerialName("creador")
    val creador: String,

    @SerialName("creador_id")
    val creadorId: String,

    @SerialName("creador_ready")
    val creadorReady: Boolean = false,

    @SerialName("retado")
    val retado: String,

    @SerialName("retado_id")
    val retadoId: String,

    @SerialName("retado_ready")
    val retadoReady: Boolean = false,

    @SerialName("user_pendiente_id")
    val userPendienteId: String? = null,

    @SerialName("esperando_confirmacion")
    val esperandoConfirmacion: Boolean = false
)