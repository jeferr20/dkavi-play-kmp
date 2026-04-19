package pe.breaker.dkaviplay.presentation.screen.registerDatos.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.component.form.FormDropdownField
import pe.breaker.dkaviplay.presentation.component.form.FormHorario
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosState

@Composable
fun DatosBillar(
    modifier: Modifier,
    state: RegisterDatosState,
    screenModel: RegisterDatosModel
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FormHorario(
            label = "Horario de juego",
            horarios = state.horarios,
            onHorarioChange = { index, dia ->
                screenModel.onHorarioChanged(index, dia)
            },
            modifier = Modifier.fillMaxWidth(),
            onAllDayToggle = { isEnabled ->
                screenModel.onAllDayToggle(isEnabled)
            },
            onFullDayClick = {index, isFullday ->
                screenModel.setAllDay(index,isFullday)
            },
            error = state.horarioError
        )

        FormDropdownField(
            label = "Billar de Preferencia",
            options = state.sedes.map { it.nombreSede },
            selectedOption = state.sedes.find { it.sedeUid == state.idSedeSeleccionada }?.nombreSede
                ?: "",
            onOptionSelected = { nombre ->
                screenModel.onSedeSelected(nombre)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.sedes.isNotEmpty(),
            error = state.sedeError
        )
    }
}