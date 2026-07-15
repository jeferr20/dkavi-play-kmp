package pe.breaker.dkaviplay.data.remote.supabase.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserQuickDTO(

    @SerialName("uuid_auth")
    val uuidAuth: String,

    @SerialName("usuario")
    val usuario: String,

    @SerialName("imagen")
    val imagen: String? = null,

    @SerialName("puntos")
    val puntos: Int,

    @SerialName("partidas_ganadas")
    val partidasGanadas: Int,

    @SerialName("partidas_jugadas")
    val partidasJugadas: Int
)