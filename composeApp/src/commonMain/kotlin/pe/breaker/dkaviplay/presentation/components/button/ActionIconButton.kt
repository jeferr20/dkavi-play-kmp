package pe.breaker.dkaviplay.presentation.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ActionIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    colorBackground: Color,
    colorIcon: Color,
    modifierIcon: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(48.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = modifierIcon,
                imageVector = icon,
                contentDescription = contentDescription,
                tint = colorIcon
            )
        }
    }
}