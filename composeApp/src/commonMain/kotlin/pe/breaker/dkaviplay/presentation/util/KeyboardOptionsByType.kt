package pe.breaker.dkaviplay.presentation.util

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

fun keyboardOptionsByType(type: InputType): KeyboardOptions {
    return when (type) {
        InputType.NOMBRE -> KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words
        )

        InputType.CORREO -> KeyboardOptions(
            keyboardType = KeyboardType.Email,
            capitalization = KeyboardCapitalization.None
        )

        InputType.CELULAR , InputType.NUMERO-> KeyboardOptions(
            keyboardType = KeyboardType.Number,
            capitalization = KeyboardCapitalization.None
        )

        InputType.TEXTO -> KeyboardOptions(
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences
        )

        InputType.PASSWORD -> KeyboardOptions(
            keyboardType = KeyboardType.Password,
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false
        )
    }
}