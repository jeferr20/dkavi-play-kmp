package pe.breaker.dkaviplay.presentation.util.lectorQR.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GalleryFrame(
    onGalleryClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, end = 24.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = onGalleryClick,
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = "Cargar desde galería",
                tint = Color.White
            )
        }
    }
}