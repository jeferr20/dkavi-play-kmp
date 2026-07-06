package pe.breaker.dkaviplay.core.data.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.breaker.dkaviplay.core.data.remote.firebase.EmpresaFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.SedeFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.SedeHorarioFirebase
import pe.breaker.dkaviplay.core.domain.model.HorarioSede
import pe.breaker.dkaviplay.core.domain.model.Sede
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun mapToSede(
    id: String,
    sede: SedeFirebase,
    empresa: EmpresaFirebase?
) : Sede {
    return Sede(
        sedeUid = id,
        empresaUid = sede.uuidEmpresa ?: "",
        nombreSede = sede.nombre ?: "",
        direccion = sede.direccion ?: "",
        horario = sede.horario?.map { it.toDomain() } ?: emptyList(),
        numSede = sede.numSede ?: "",
        referencia = sede.referencia ?: "",
        nombreEmpresa = empresa?.nombre ?: "",
        logo = empresa?.logo ?: "",
        ruc = empresa?.ruc ?: "",
        latitud = sede.latitud ?: 0.0,
        longitud = sede.longitud ?: 0.0
    )
}

fun SedeHorarioFirebase.toDomain(): HorarioSede {
    return HorarioSede(
        dia = this.dia ?: "",
        inicio = this.inicio ?: "",
        fin = this.fin ?: "",
        activo = this.activo ?: false
    )
}

@OptIn(ExperimentalTime::class)
fun List<HorarioSede>.findTodaySchedule(): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek

    val dayName = when(today.name) {
        "MONDAY" -> "Lunes"
        "TUESDAY" -> "Martes"
        "WEDNESDAY" -> "Miércoles"
        "THURSDAY" -> "Jueves"
        "FRIDAY" -> "Viernes"
        "SATURDAY" -> "Sábado"
        "SUNDAY" -> "Domingo"
        else -> ""
    }

    val horarioHoy = this.find { it.dia.contains(dayName, ignoreCase = true) }

    return if (horarioHoy == null || !horarioHoy.activo) {
        "Cerrado hoy"
    } else {
        "${horarioHoy.inicio} - ${horarioHoy.fin}"
    }
}