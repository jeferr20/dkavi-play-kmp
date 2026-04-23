package pe.breaker.dkaviplay.domain.animations

import androidx.compose.ui.graphics.Color

data class ChallengeSpark(
    val id: Int,
    val angle: Double,
    val speed: Float,
    val color: Color = Color(0xFF00FFFF)
)