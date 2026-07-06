package pe.breaker.dkaviplay.core.domain.model

data class Sede(
    val sedeUid: String,
    val empresaUid: String,
    val nombreSede: String,
    val direccion: String,
    val horario: List<HorarioSede>,
    val numSede: String,
    val referencia: String,
    val nombreEmpresa: String,
    val logo: String,
    val ruc: String,
    val latitud: Double,
    val longitud: Double
)