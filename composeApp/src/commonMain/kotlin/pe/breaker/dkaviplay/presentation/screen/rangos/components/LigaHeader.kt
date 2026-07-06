package pe.breaker.dkaviplay.presentation.screen.rangos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun LigaHeader(titulo: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = titulo,
            color = colorPrimary.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        )
    }
}