package pe.breaker.dkaviplay.presentation.screen.registerDatos.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.components.form.FormTextField
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosState
import pe.breaker.dkaviplay.presentation.util.InputType

@Composable
fun DatosPersonales(
    modifier: Modifier,
    state: RegisterDatosState,
    screenModel: RegisterDatosModel,
    isLogged: Boolean
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FormTextField(
            label = "Nombres",
            value = state.nombres ?: "",
            icon = Icons.Default.Person,
            onValueChange = { newValue ->
                screenModel.onFieldChanged {
                    copy(nombres = newValue, nombresError = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Nombres",
            isPassword = false,
            inputType = InputType.TEXTO,
            error = state.nombresError,
            enabled = !isLogged
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormTextField(
                label = "Apellido Paterno",
                value = state.apellidoPaterno ?: "",
                icon = Icons.Default.Person,
                onValueChange = { newValue ->
                    screenModel.onFieldChanged {
                        copy(
                            apellidoPaterno = newValue,
                            apellidoPaternoError = null
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = "Paterno",
                isPassword = false,
                inputType = InputType.TEXTO,
                error = state.apellidoPaternoError,
                enabled = !isLogged
            )

            FormTextField(
                label = "Apellido Materno",
                value = state.apellidoMaterno ?: "",
                icon = Icons.Default.Person,
                onValueChange = { newValue ->
                    screenModel.onFieldChanged {
                        copy(
                            apellidoMaterno = newValue,
                            apellidoMaternoError = null
                        )
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = "Materno",
                isPassword = false,
                inputType = InputType.TEXTO,
                error = state.apellidoMaternoError,
                enabled = !isLogged
            )
        }
    }
}