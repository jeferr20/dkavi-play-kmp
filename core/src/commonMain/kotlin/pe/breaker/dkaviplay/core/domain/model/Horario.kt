package pe.breaker.dkaviplay.core.domain.model

data class Horario(
    val nombre: String,
    val habilitado: Boolean,
    val horaInicio: String,
    val horaFin: String
)
