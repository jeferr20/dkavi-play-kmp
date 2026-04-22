package pe.breaker.dkaviplay.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.components.button.ActionIconButton
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun CustomAppbar(
    modifier: Modifier = Modifier,
    text: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        ActionIconButton(
            icon = Icons.Filled.ArrowBackIosNew,
            contentDescription = null,
            onClick = { onClick() },
            colorIcon = colorPrimary,
            colorBackground = colorBlackSurface,
            modifier = Modifier.align(Alignment.CenterStart),
            modifierIcon = Modifier.offset(x = (-2).dp)
        )

        text?.let{
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = it,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}