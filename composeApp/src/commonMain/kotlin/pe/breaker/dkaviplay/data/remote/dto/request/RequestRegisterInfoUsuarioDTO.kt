package pe.breaker.dkaviplay.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestRegisterInfoUsuarioDTO(
    val usuarioUid: Int,
    val nombres: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val correo: String,
    val genero: String,
    val celular: String,
    val fechaNacimiento: String?,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val horarios: List<RequestHorarioDTO>,
    val sedePreferencia: String
)