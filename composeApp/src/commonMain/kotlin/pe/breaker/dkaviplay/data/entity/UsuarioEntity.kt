package pe.breaker.dkaviplay.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsuarioEntity (
    @SerialName("i_usuario_id")
    val usuarioUid : Int,

    @SerialName("vc_uid_auth")
    val uidAuth : String,

    @SerialName("vc_usuario")
    val usuario : String,

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

    @SerialName("i_monedas")
    val monedas: Int,

    @SerialName("vc_nombres")
    val nombres: String,

    @SerialName("vc_apellidoPaterno")
    val apellidoPaterno: String,

    @SerialName("vc_apellidoMaterno")
    val apellidoMaterno: String,

    @SerialName("vc_celular")
    val celular: String,

    @SerialName("vc_correo")
    val correo: String,
)