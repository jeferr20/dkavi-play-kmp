package pe.breaker.dkaviplay.core.data.mapper

import pe.breaker.dkaviplay.core.data.remote.firebase.UserHorarioFirebase
import pe.breaker.dkaviplay.core.domain.model.DiaHorarioState
import pe.breaker.dkaviplay.core.domain.model.Horario

fun DiaHorarioState.toDTO(): UserHorarioFirebase {
    return UserHorarioFirebase(
        nombre = nombre,
        habilitado = habilitado,
        horaInicio = horaInicio,
        horaFin = horaFin
    )
}

fun Horario.toState(): DiaHorarioState {
    return DiaHorarioState(
        nombre = nombre,
        habilitado = habilitado,
        horaInicio = horaInicio,
        horaFin = horaFin,
    )
}