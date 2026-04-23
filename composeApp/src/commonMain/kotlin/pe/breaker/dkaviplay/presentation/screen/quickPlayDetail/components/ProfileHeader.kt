package pe.breaker.dkaviplay.presentation.screen.quickPlayDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun ProfileHeader(imageUrl: String?, nombre: String, rango: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.DarkGray),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(120.dp).clip(CircleShape),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = "Rango: $rango", fontSize = 16.sp, color = colorPrimary)
    }
}