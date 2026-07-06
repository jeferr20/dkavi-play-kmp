package pe.breaker.dkaviplay.core.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class UserHorarioFirebase(
    val habilitado: Boolean?,
    val horaInicio: String?,
    val horaFin: String?,
    val nombre: String?
)