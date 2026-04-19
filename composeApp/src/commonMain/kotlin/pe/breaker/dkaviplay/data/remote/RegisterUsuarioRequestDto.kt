package pe.breaker.dkaviplay.data.remote

import kotlinx.serialization.Serializable
import pe.breaker.dkaviplay.data.remote.firebase.UserHorarioFirebase

@Serializable
data class RegisterUsuarioRequestDto(
    val usuarioUid: String,
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
    val horarios: List<UserHorarioFirebase>,
    val sedePreferencia: String
)