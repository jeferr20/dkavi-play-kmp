package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserMovilDto(
    @SerialName("id")
    val id: Long, // int8 Primary Key Identity

    @SerialName("uuid_auth")
    val uuidAuth: String? = null, // uuid Nullable

    @SerialName("password")
    val password: String? = null, // text Nullable

    @SerialName("usuario")
    val usuario: String? = null, // text Nullable

    @SerialName("departamento")
    val departamento: String? = null, // text Nullable

    @SerialName("distrito")
    val distrito: String? = null, // text Nullable

    @SerialName("provincia")
    val provincia: String? = null, // text Nullable

    @SerialName("genero")
    val genero: String? = null, // text Nullable

    @SerialName("fechaNacimiento")
    val fechaNacimiento: Long? = null, // numeric Nullable (Timestamp ms)

    @SerialName("partidasGanadas")
    val partidasGanadas: Int? = null, // int2 Nullable

    @SerialName("partidasJugadas")
    val partidasJugadas: Int? = null, // int2 Nullable

    @SerialName("puntos")
    val puntos: Long? = null, // int8 Nullable

    @SerialName("ladderRank")
    val ladderRank: Double? = null, // float8 Nullable

    @SerialName("sedePreferencia")
    val sedePreferencia: String? = null, // text Nullable

    @SerialName("urlImagen")
    val urlImagen: String? = null, // text Nullable

    @SerialName("fcmToken")
    val fcmToken: String? = null, // text Nullable

    @SerialName("status")
    val status: Boolean, // bool Non-nullable

    @SerialName("date_cr")
    val dateCr: String, // timestamptz Non-nullable

    @SerialName("date_up")
    val dateUp: String? = null, // timestamptz Nullable

    @SerialName("monedas")
    val monedas: Long? = null, // int8 Nullable

    @SerialName("completo")
    val completo: Boolean? = null, // bool Nullable

    @SerialName("nombres")
    val nombres: String? = null, // text Nullable

    @SerialName("apellidoPaterno")
    val apellidoPaterno: String? = null, // text Nullable

    @SerialName("apellidoMaterno")
    val apellidoMaterno: String? = null, // text Nullable

    @SerialName("celular")
    val celular: String? = null, // text Nullable

    @SerialName("correo")
    val correo: String? = null // text Nullable
)