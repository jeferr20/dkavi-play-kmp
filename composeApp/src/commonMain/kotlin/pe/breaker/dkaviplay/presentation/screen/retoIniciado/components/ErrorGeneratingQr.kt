package pe.breaker.dkaviplay.presentation.screen.retoIniciado.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorGeneratingQr() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Share, contentDescription = null, tint = Color.Red, modifier = Modifier.size(40.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("No se pudo generar el QR", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}