package pe.breaker.dkaviplay.presentation.screen.registerReserva.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import pe.breaker.dkaviplay.core.domain.model.Sede

@Composable
fun CardEmpresa(
    modifier: Modifier = Modifier,
    sede: Sede
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        border = BorderStroke(
            width = 0.5.dp,
            color = Color.White.copy(alpha = 0.8f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = sede.logo,
                contentDescription = "Imagen sede",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                onLoading = {
                    println("DEBUG_MAP: Cargando imagen...")
                },
                onSuccess = {
                    println("DEBUG_MAP: Imagen cargada con éxito")
                },
                onError = { error ->
                    println("DEBUG_MAP: Error en Coil: ${error.result.throwable.message}")
                    error.result.throwable.printStackTrace()
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 300f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = sede.nombreSede,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = sede.nombreEmpresa,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}