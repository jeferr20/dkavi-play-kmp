package pe.breaker.dkaviplay.data.entity

import kotlinx.serialization.SerialName

data class PersonaEntity (
    @SerialName("vc_persona_uid")
    val personaUid: String,

    @SerialName("vc_nombres")
    val nombres: String,

    @SerialName("vc_apellido_paterno")
    val apellidoPaterno: String,

    @SerialName("vc_apellido_materno")
    val apellidoMaterno: String,

    @SerialName("vc_celular")
    val celular: String,

    @SerialName("vc_correo")
    val correo: String,
)