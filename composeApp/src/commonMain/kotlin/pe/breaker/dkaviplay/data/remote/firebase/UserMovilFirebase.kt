package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class UserMovilFirebase(
    val userUid: String? = null,
    val user: String,
    val urlImagen: String? = null,
    val horarios: List<UserHorarioFirebase>? = null,
    val partidasGanadas: Int,
    val partidasJugadas: Int,
    val puntos: Int,
    val rol: String,
    val inventario: List<InventarioFirebase> = emptyList(),
    val genero: String?,
    val departamento: String?,
    val distrito: String?,
    val provincia: String?,
    val sedePreferencia: String?,
)