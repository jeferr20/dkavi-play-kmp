package pe.breaker.dkaviplay.domain.animations

import androidx.compose.ui.graphics.Color

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float, // Velocidad X
    val vy: Float, // Velocidad Y
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean,
    val alpha: Float = 1f
)