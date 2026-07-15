package pe.breaker.dkaviplay.data.entity

data class HorarioEntity(
    val id:Int,
    val usuarioUid: Int,
    val dia: Int,
    val horaInicio: String,
    val horaFin: String,
    val habilitado: Boolean
)