package pe.breaker.dkaviplay.presentation.screen.registerDatos.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.component.form.FormDropdownField
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosState

@Composable
fun DatosUbigeo(
    modifier: Modifier,
    state: RegisterDatosState,
    screenModel: RegisterDatosModel
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FormDropdownField(
            label = "Departamento",
            options = state.departamentos.map { it.nombre },
            selectedOption = state.departamentos.find { it.id == state.idDepartamento }?.nombre
                ?: "",
            onOptionSelected = { nombre ->
                val id = state.departamentos.find { it.nombre == nombre }?.id ?: ""
                screenModel.onDepartamentoSelected(id)
            },
            modifier = Modifier.fillMaxWidth(),
            error = state.departamentoError
        )

        FormDropdownField(
            label = "Provincia",
            options = state.provincias.map { it.nombre },
            selectedOption = state.provincias.find { it.id == state.idProvincia }?.nombre
                ?: "",
            onOptionSelected = { nombre ->
                val id = state.provincias.find { it.nombre == nombre }?.id ?: ""
                screenModel.onProvinciaSelected(id)
            },
            enabled = state.provincias.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            error = state.provinciaError
        )

        FormDropdownField(
            label = "Distrito",
            options = state.distritos.map { it.nombre },
            selectedOption = state.distritos.find { it.id == state.idDistrito }?.nombre
                ?: "",
            onOptionSelected = { nombre ->
                val id = state.distritos.find { it.nombre == nombre }?.id ?: ""
                screenModel.onDistritoSelected(id)
            },
            enabled = state.distritos.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            error = state.distritoError
        )
    }
}