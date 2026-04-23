package pe.breaker.dkaviplay.presentation.util.lectorQR.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ScannerFrame(modifier: Modifier) {
    val colorPrimary = Color(0xFF2D5fb4)
    val infiniteTransition = rememberInfiniteTransition()

    // Animación de la línea láser
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 4.dp.toPx()
        val cornerLength = 30.dp.toPx()
        val sizeWidth = size.width
        val sizeHeight = size.height

        // --- DIBUJAR LAS ESQUINAS ---
        val path = Path().apply {
            // Arriba Izquierda
            moveTo(0f, cornerLength)
            lineTo(0f, 0f)
            lineTo(cornerLength, 0f)

            // Arriba Derecha
            moveTo(sizeWidth - cornerLength, 0f)
            lineTo(sizeWidth, 0f)
            lineTo(sizeWidth, cornerLength)

            // Abajo Izquierda
            moveTo(0f, sizeHeight - cornerLength)
            lineTo(0f, sizeHeight)
            lineTo(cornerLength, sizeHeight)

            // Abajo Derecha
            moveTo(sizeWidth - cornerLength, sizeHeight)
            lineTo(sizeWidth, sizeHeight)
            lineTo(sizeWidth, sizeHeight - cornerLength)
        }

        drawPath(
            path = path,
            color = colorPrimary,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // --- LÍNEA LÁSER ANIMADA ---
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    colorPrimary,
                    Color.Transparent
                )
            ),
            start = Offset(x = sizeWidth * 0.1f, y = sizeHeight * laserOffset),
            end = Offset(x = sizeWidth * 0.9f, y = sizeHeight * laserOffset),
            strokeWidth = 2.dp.toPx()
        )
    }
}