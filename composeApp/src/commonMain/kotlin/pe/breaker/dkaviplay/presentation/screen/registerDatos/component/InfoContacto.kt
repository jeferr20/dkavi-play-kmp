package pe.breaker.dkaviplay.presentation.screen.registerDatos.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.component.form.FormDateField
import pe.breaker.dkaviplay.presentation.component.form.FormRadioButton
import pe.breaker.dkaviplay.presentation.component.form.FormTextField
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosState
import pe.breaker.dkaviplay.presentation.util.InputType
import pe.breaker.dkaviplay.presentation.util.formatMillisToDate

@Composable
fun InfoContacto(
    modifier: Modifier,
    state : RegisterDatosState,
    screenModel: RegisterDatosModel,
    isLogged: Boolean
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        FormTextField(
            label = "Correo Electrónico",
            value = state.correo ?: "",
            icon = Icons.Default.Mail,
            onValueChange = { newValue ->
                screenModel.onFieldChanged {
                    copy(correo = newValue, correoError = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Correo Electrónico",
            isPassword = false,
            inputType = InputType.CORREO,
            error = state.correoError
        )

        FormTextField(
            label = "Celular",
            value = state.celular ?: "",
            icon = Icons.Default.Phone,
            onValueChange = { newValue ->
                screenModel.onFieldChanged {
                    copy(celular = newValue, celularError = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Celular",
            isPassword = false,
            inputType = InputType.CELULAR,
            error = state.celularError
        )

        if(!isLogged){
            FormDateField(
                modifier = Modifier.fillMaxWidth(),
                label = "Fecha de nacimiento",
                value = state.fechaNacimiento ?: "",
                onDateSelected = { newValue ->
                    newValue?.let {
                        val formatedFecha = formatMillisToDate(it)
                        screenModel.onFieldChanged {
                            copy(
                                fechaNacimiento = formatedFecha,
                                fechaNacimientoError = null
                            )
                        }
                    }
                },
                restringirFechaActual = false,
                error = state.fechaNacimientoError
            )
        }

        FormRadioButton(
            label = "Género",
            options = listOf("Masculino", "Femenino", "Otro"),
            selectedOption = state.genero ?: "",
            onOptionSelected = { newValue ->
                screenModel.onFieldChanged {
                    copy(genero = newValue, generoError = null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            error = state.generoError
        )
    }
}