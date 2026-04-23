package pe.breaker.dkaviplay.presentation.util

import androidx.compose.ui.graphics.Color
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

fun String.toColor(): Color {
    val hex = this.replace("#", "")
    return when (hex.length) {
        6 -> Color(
            red = hex.take(2).toInt(16) / 255f,
            green = hex.substring(2, 4).toInt(16) / 255f,
            blue = hex.substring(4, 6).toInt(16) / 255f,
            alpha = 1f
        )
        8 -> Color(
            red = hex.substring(2, 4).toInt(16) / 255f,
            green = hex.substring(4, 6).toInt(16) / 255f,
            blue = hex.substring(6, 8).toInt(16) / 255f,
            alpha = hex.take(2).toInt(16) / 255f
        )
        else -> colorPrimary
    }
}