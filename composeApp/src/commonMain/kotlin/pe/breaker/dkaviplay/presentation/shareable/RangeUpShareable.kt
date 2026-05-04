package pe.breaker.dkaviplay.presentation.shareable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper

@Composable
fun RankUpShareable(
    userName: String,
    userUrlImage: String?,
    nuevoRango: String?,
    modifier: Modifier = Modifier
) {
    val rankRes = RankResourceMapper.getDrawableByRank(nuevoRango)

    // Degradado de éxito (Dorado/Naranja)
    val rankUpGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFD700).copy(alpha = 0.2f), Color.Black)
    )

    Card(
        modifier = modifier.size(350.dp, 550.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Fondo con brillo superior
            Box(modifier = Modifier.fillMaxSize().background(rankUpGradient))

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // --- PARTE SUPERIOR: INFO USUARIO ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {

                        if (userUrlImage.isNullOrEmpty()) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(90.dp)
                            )
                        } else {
                            AsyncImage(
                                model = userUrlImage,
                                contentDescription = userName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // --- PARTE CENTRAL: EL ASCENSO ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = GoldColor,
                        modifier = Modifier.size(50.dp)
                    )

                    Text(
                        text = "¡NUEVO RANGO!",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Icono de Rango con Efecto Glow
                    Box(contentAlignment = Alignment.Center) {
                        // Resplandor de fondo
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .background(GoldColor.copy(alpha = 0.15f), CircleShape)
                                .blur(30.dp)
                        )

                        Image(
                            painter = painterResource(rankRes),
                            contentDescription = nuevoRango,
                            modifier = Modifier.size(160.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = nuevoRango?.uppercase() ?: "",
                        color = GoldColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                // --- PARTE INFERIOR: FOOTER ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¡Sigue dominando la mesa!",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "DKAVI PLAY",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.labelLarge,
                        letterSpacing = 4.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RankUpPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        RankUpShareable(
            userName = "Maciso",
            userUrlImage = null,
            nuevoRango = "Arcángel"
        )
    }
}