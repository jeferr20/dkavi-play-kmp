package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class PersonaFirebase(
    val personaUid: String?,
    val nombres: String?,
    val apellidoPaterno: String?,
    val apellidoMaterno: String?,
    val celular: String?,
    val correo: String?,
    val codigoExpiracion: Long?,
    val codigoRecuperacion: String?
)