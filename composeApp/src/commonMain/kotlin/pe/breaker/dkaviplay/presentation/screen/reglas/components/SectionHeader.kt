package pe.breaker.dkaviplay.presentation.screen.reglas.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun SectionHeader(
    modifier: Modifier,
    text: String,
    icon: ImageVector,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text.uppercase(),
            color = colorPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}