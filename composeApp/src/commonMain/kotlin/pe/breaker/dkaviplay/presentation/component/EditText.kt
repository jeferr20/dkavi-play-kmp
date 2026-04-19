package pe.breaker.dkaviplay.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.InputType
import pe.breaker.dkaviplay.presentation.util.filterInputByType
import pe.breaker.dkaviplay.presentation.util.keyboardOptionsByType

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null,
    inputType: InputType,
    isPassword: Boolean = false,
    error: String? = null,
    enabled: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(if (enabled) 1f else 0.4f)
    val borderColor by animateColorAsState(
        when {
            error != null -> Color.Red
            !enabled -> Color.White.copy(alpha = 0.05f)
            isFocused -> colorPrimary
            else -> Color.White.copy(alpha = 0.2f)
        }
    )
    val backgroundColor = if (enabled) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.02f)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
                .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                .background(backgroundColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize().graphicsLayer(alpha = contentAlpha)
            ) {
                // Icono Principal
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = colorPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Usamos BasicTextField para eliminar los paddings ocultos de Material
                BasicTextField(
                    value = value,
                    enabled = enabled,
                    onValueChange = {
                        val filtered = filterInputByType(inputType, it)
                        onValueChange(filtered)
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = if (enabled) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = keyboardOptionsByType(inputType),
                    visualTransformation = if (isPassword && !passwordVisible)
                        PasswordVisualTransformation()
                    else VisualTransformation.None,
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Icono de Password
                if (isPassword) {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        error?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = error,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}