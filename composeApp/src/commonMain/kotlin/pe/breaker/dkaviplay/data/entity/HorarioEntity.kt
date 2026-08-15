package pe.breaker.dkaviplay.data.entity

data class HorarioEntity(
    val id: Long,
    val usuarioUid: Long,
    val dia: Int,
    val horaInicio: String,
    val horaFin: String,
    val habilitado: Boolean
)