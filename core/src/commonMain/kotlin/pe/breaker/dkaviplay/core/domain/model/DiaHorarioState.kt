package pe.breaker.dkaviplay.core.domain.model

data class DiaHorarioState(
    val nombre: String,
    val habilitado: Boolean = false,
    val horaInicio: String = "08:00",
    val horaFin: String = "22:00"
)