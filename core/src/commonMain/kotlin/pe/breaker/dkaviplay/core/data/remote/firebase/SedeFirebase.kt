package pe.breaker.dkaviplay.core.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class SedeFirebase (
    val sedeUid: String?,
    val departamento: String?,
    val direccion: String?,
    val distrito: String?,
    val horario: List<SedeHorarioFirebase>?,
    val imagen: String?,
    val latitud: Double?,
    val longitud: Double?,
    val nombre: String?,
    val numSede: String?,
    val provincia: String?,
    val referencia: String?,
    val uuidEmpresa: String?
)