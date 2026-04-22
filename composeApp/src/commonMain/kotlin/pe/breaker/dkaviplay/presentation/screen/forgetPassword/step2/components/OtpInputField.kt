package pe.breaker.dkaviplay.presentation.screen.forgetPassword.step2.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OtpInputField(
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    // Usamos TextFieldValue para controlar la posición del cursor (Selection)
    val textFieldValue = remember(code) {
        TextFieldValue(
            text = code,
            // Forzamos el cursor siempre al final de lo que esté escrito
            selection = TextRange(code.length)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusRequester.requestFocus()
            },
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                // Solo notificamos el cambio de texto al modelo
                if (it.text.length <= 6) {
                    onCodeChange(it.text)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .alpha(0.01f), // Casi invisible pero existente para el sistema
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(Color.Transparent),
            textStyle = TextStyle(fontSize = 0.sp, color = Color.Transparent),
            // Esto evita que aparezca el menú de "Copiar/Pegar" y los indicadores
            decorationBox = { innerTextField ->
                Box(Modifier.fillMaxSize()) {
                    innerTextField()
                }
            }
        )

        // Fila visual de cuadros (Tu código actual)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                val char = code.getOrNull(index)?.toString() ?: ""
                val isFocused = if (code.length == 6) index == 5 else code.length == index
                OtpCell(value = char, isFocused = isFocused)
            }
        }
    }
}