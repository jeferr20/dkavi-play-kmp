package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.data.remote.firebase.UserHorarioFirebase
import pe.breaker.dkaviplay.presentation.screen.registerDatos.DiaHorarioState

fun DiaHorarioState.toDTO(): UserHorarioFirebase {
    return UserHorarioFirebase(
        nombre = nombre,
        habilitado = habilitado,
        horaInicio = horaInicio,
        horaFin = horaFin
    )
}

fun UserHorarioFirebase.toState(): DiaHorarioState {
    return DiaHorarioState(
        nombre = nombre ?: "",
        habilitado = habilitado ?: false,
        horaInicio = horaInicio ?: "",
        horaFin = horaFin ?: "",
    )
}