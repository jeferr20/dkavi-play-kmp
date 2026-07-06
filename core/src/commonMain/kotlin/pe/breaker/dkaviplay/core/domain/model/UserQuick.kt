package pe.breaker.dkaviplay.core.domain.model

data class UserQuick(
    val userUid: String? = null,
    val usuario: String,
    val imagen: String? = null,
    val rango: String,
    val horarios: List<Horario>? = null,
    val partidasGanadas: Int,
    val partidasJugadas: Int,
    val puntos: Int
)