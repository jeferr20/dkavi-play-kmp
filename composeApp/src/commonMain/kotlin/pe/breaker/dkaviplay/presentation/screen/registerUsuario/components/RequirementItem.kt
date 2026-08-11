package pe.breaker.dkaviplay.presentation.screen.registerUsuario.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RequirementItem(
    text: String,
    isSatisfied: Boolean
) {
    val color by animateColorAsState(
        targetValue = if (isSatisfied) Color(0xFF4CAF50) else Color.Gray.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isSatisfied) Icons.Default.CheckCircle else Icons.Default.Circle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSatisfied) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}