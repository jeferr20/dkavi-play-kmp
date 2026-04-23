package pe.breaker.dkaviplay.presentation.animations.animations.getItem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun BilliardBackground(auraScale: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(0.0f to Color(0xFF1A1A2E), 1.0f to Color.Black)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(300.dp)
                .graphicsLayer { scaleX = auraScale; scaleY = auraScale; alpha = 0.3f }
                .background(Brush.radialGradient(listOf(Color(0xFF2196F3), Color.Transparent)), CircleShape)
        )
    }
}