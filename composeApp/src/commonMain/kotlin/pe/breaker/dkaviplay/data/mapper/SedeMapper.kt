package pe.breaker.dkaviplay.data.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.breaker.dkaviplay.data.remote.firebase.EmpresaFirebase
import pe.breaker.dkaviplay.data.remote.firebase.SedeFirebase
import pe.breaker.dkaviplay.data.remote.firebase.SedeHorarioFirebase
import pe.breaker.dkaviplay.data.remote.supabase.HorarioSedeDTO
import pe.breaker.dkaviplay.data.remote.supabase.view.SedeEmpresaView
import pe.breaker.dkaviplay.domain.model.HorarioSede
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.util.Dias
import kotlin.time.Clock

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

fun List<HorarioSede>.findTodaySchedule(): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek

    val dayName = Dias.fromDayOfWeek(today)?.dia.orEmpty()

    val horarioHoy = this.find { it.dia.contains(dayName, ignoreCase = true) }

    return if (horarioHoy == null || !horarioHoy.activo) {
        "Cerrado hoy"
    } else {
        "${horarioHoy.inicio} - ${horarioHoy.fin}"
    }
}

fun SedeEmpresaView.toDomain(): Sede {
    return Sede(
        sedeUid = this.sedeId.toString(),
        empresaUid = this.empresaId.toString(),
        nombreSede = this.nombreSede,
        direccion = this.direccion ?: "",
        numSede = this.numSede ?: "",
        referencia = this.referencia ?: "",
        nombreEmpresa = this.nombreEmpresa,
        logo = this.logo ?: "",
        ruc = this.ruc ?: "",
        latitud = this.latitud,
        longitud = this.longitud,
        horario = this.horarios.map { it.toDomain() }
    )
}

fun HorarioSedeDTO.toDomain(): HorarioSede {
    return HorarioSede(
        activo = this.activo,
        dia = this.dia,
        fin = this.fin,
        inicio = this.inicio
    )
}