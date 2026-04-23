package pe.breaker.dkaviplay.presentation.animations.animations.getItem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HitIndicator(taps: Int, visible: Boolean) {
    AnimatedVisibility(visible = visible, exit = fadeOut()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "GOLPEA PARA ROMPER",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(3) { i ->
                    val hit = i < taps
                    Box(
                        Modifier
                            .size(width = 40.dp, height = 6.dp)
                            .clip(CircleShape)
                            .background(if (hit) Color(0xFFE94560) else Color.White.copy(0.2f))
                    )
                }
            }
        }
    }
}