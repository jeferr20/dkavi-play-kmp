package pe.breaker.dkaviplay.presentation.screen.perfil.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = colorPrimary.copy(alpha = 0.7f),
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
        fontWeight = FontWeight.Bold
    )
}