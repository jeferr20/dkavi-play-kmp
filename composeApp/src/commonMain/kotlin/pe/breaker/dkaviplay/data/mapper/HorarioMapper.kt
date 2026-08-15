package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.cache.HorarioTable
import pe.breaker.dkaviplay.data.entity.HorarioEntity
import pe.breaker.dkaviplay.data.remote.dto.request.RequestHorarioDTO
import pe.breaker.dkaviplay.data.remote.supabase.UsuarioHorarioDTO
import pe.breaker.dkaviplay.presentation.screen.registerDatos.DiaHorarioState
import pe.breaker.dkaviplay.util.Dias

fun HorarioEntity.toTable(): HorarioTable {
    return HorarioTable(
        id = this.id,
        usuarioUid = this.usuarioUid,
        dia = this.dia.toLong(),
        horaInicio = this.horaInicio,
        horaFin = this.horaFin,
        habilitado = if(this.habilitado) 1L else 0L
    )
}

fun HorarioTable.toState() : DiaHorarioState {
    return DiaHorarioState(
        nombre = Dias.fromNumero(this.dia.toInt())?.dia ?: "",
        habilitado = this.habilitado == 1L,
        horaInicio = this.horaInicio.take(5),
        horaFin = this.horaFin.take(5)
    )
}

fun DiaHorarioState.toDTO() : RequestHorarioDTO{
    return RequestHorarioDTO(
        habilitado = this.habilitado,
        horaInicio = this.horaInicio,
        horaFin = this.horaFin,
        dia = Dias.fromNombre(this.nombre)?.nDia ?: 0
    )
}

fun UsuarioHorarioDTO.toEntity() : HorarioEntity{
    return HorarioEntity(
        id = this.id,
        usuarioUid = this.userMovilId ?: 0L,
        dia = this.dia ?: 0,
        horaInicio = this.horaInicio ?: "",
        horaFin = this.horaFin ?: "",
        habilitado = this.habilitado
    )
}