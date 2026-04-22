package pe.breaker.dkaviplay.presentation.screen.aceptarReto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun RetadorHeader(retador: UserQuick) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            // Avatar con Fallback
            if (!retador.imagen.isNullOrBlank()) {
                AsyncImage(
                    model = retador.imagen,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(3.dp, colorPrimary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(3.dp, colorPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(80.dp))
                }
            }

            // Badge de Rango
            Surface(
                color = colorPrimary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.offset(y = 14.dp),
                border = BorderStroke(2.dp, colorBlackSurface)
            ) {
                Text(
                    text = retador.rango.uppercase(),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    ),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = retador.usuario.uppercase(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            ),
            color = Color.White
        )
        Text(
            text = "TE HA RETADO A UN DUELO",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = colorPrimary.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
        )
    }
}