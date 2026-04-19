package pe.breaker.dkaviplay.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.theme.colorRedError
import pe.breaker.dkaviplay.presentation.theme.colorWarning

enum class StatusUiType(
    val title: String,
    val icon: ImageVector,
    val color: Color
) {
    SUCCESS("¡Éxito!", Icons.Rounded.CheckCircle, colorPrimary),
    ERROR("¡Error!", Icons.Rounded.Cancel, colorRedError),
    WARNING("¡Atención!", Icons.Rounded.ErrorOutline, colorWarning),
}