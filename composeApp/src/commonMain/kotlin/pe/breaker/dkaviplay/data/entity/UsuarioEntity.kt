package pe.breaker.dkaviplay.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioEntity (
    @SerialName("vc_usuario_uid")
    val usuarioUid : String,

    @SerialName("vc_usuario")
    val usuario : String,

    @SerialName("vc_rango")
    val rango : String,

    @SerialName("i_puntos")
    val puntos : Int,

    @SerialName("i_partidas_ganadas")
    val partidasGanadas : Int,

    @SerialName("i_partidas_jugadas")
    val partidasJugadas : Int,

    @SerialName("vc_urlImagen")
    val urlImagen: String?,

    @SerialName("vc_rol")
    val rol: String,

    @SerialName("vc_inventario")
    val inventarioJson: String,

    @SerialName("vc_genero")
    val genero: String,

    @SerialName("vc_departamento")
    val departamento: String,

    @SerialName("vc_distrito")
    val distrito: String,

    @SerialName("vc_provincia")
    val provincia: String,

    @SerialName("vc_sedePreferencia")
    val sedePreferencia: String,

    @SerialName("vc_horarios")
    val horariosJson: String,

    @SerialName("i_monedas")
    val monedas: Int,
)