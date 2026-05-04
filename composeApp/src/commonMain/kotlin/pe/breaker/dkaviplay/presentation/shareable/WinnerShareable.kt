package pe.breaker.dkaviplay.presentation.shareable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dkaviplay.composeapp.generated.resources.Res
import dkaviplay.composeapp.generated.resources.Trofeo
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper
import kotlin.random.Random

val GoldColor = Color(0xFFFFD700)

//val GoldColor = colorPrimary
val DarkGrayColor = Color(0xFF2A2A2A)
val LightGrayColor = Color(0xFFBDBDBD)
val WinnerCardBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFF000000), Color(0xFF000000), Color(0xFF000000))
)
val DepthOverlay = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color.Black.copy(alpha = 0.2f),
        Color.Black.copy(alpha = 0.5f)
    )
)
val GoldGlow = Brush.radialGradient(
    colors = listOf(
        GoldColor.copy(alpha = 0.45f),
        GoldColor.copy(alpha = 0.25f),
        GoldColor.copy(alpha = 0.10f),
        Color.Transparent
    ),
    radius = 900f
)

@Composable
fun GlowingStar(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // Efecto de brillo (Glow) posterior
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = GoldColor.copy(alpha = 0.3f),
            modifier = Modifier.size(55.dp).blur(8.dp)
        )
        // Estrella Principal
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Trophy",
            tint = GoldColor,
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
fun WinnerShareable(
    ganadorName: String,
    perderdorNamer: String,
    ganadorRank: String,
    perdedorRank: String,
    ganadorUrlImage: String?,
    perdedorUrlImagen: String?,
    modifier: Modifier = Modifier
) {
    val winnerRankRes = RankResourceMapper.getDrawableByRank(ganadorRank)
    val loserRankRes = RankResourceMapper.getDrawableByRank(perdedorRank)

    Card(
        modifier = modifier.size(350.dp, 550.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WinnerCardBackground)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GoldGlow)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DepthOverlay)
            )
            // --- CAPA 1: CONFETI (DIBUJADO AL FONDO) ---
            // Al estar primero aquí, nada de lo que sigue será tapado por el confeti
            repeat(40) {
                val xPos = Random.nextFloat()
                val yPos = Random.nextFloat()
                val size = Random.nextInt(4, 10).dp
                Box(
                    modifier = Modifier
                        .offset(x = (xPos * 350).dp, y = (yPos * 550).dp)
                        .size(size)
                        .rotate(Random.nextFloat() * 360)
                        .background(
                            color = if (it % 2 == 0) GoldColor.copy(alpha = 0.6f) else Color.White.copy(
                                alpha = 0.4f
                            ),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }

            // --- CAPA 2: CONTENIDO PRINCIPAL (DIBUJADO ENCIMA) ---
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SECCIÓN SUPERIOR: ESTRELLA BRILLANTE
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlowingStar()
                    Text(
                        text = "GANADOR",
                        color = GoldColor,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    )
                    GlowingStar()
                }
                Image(
                    painter = painterResource(Res.drawable.Trofeo),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SECCIÓN CENTRAL: EL CAMPEÓN
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center) {
                        // Imagen de perfil con manejo de null
                        AsyncImage(
                            model = ganadorUrlImage,
                            contentDescription = "Ganador",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, GoldColor, CircleShape)
                                .background(DarkGrayColor)
                        )

                        if (ganadorUrlImage == null) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = ganadorName.uppercase(),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(winnerRankRes),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = ganadorRank, color = GoldColor, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // SECCIÓN INFERIOR: PERDEDOR
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f)), // Más oscuro para contraste
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                        .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = perdedorUrlImagen,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(50.dp).clip(CircleShape)
                                    .background(Color.Gray)
                            )
                            if (perdedorUrlImagen == null) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    null,
                                    tint = Color.DarkGray,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = perderdorNamer,
                                color = LightGrayColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(
                                        loserRankRes
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = perdedorRank, color = Color.Gray, fontSize = 14.sp)
                            }
                        }

                        Text(
                            text = "Perdedor",
                            color = Color.Red.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "DKAVI PLAY",
                    color = GoldColor.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 4.sp
                )
            }
        }
    }
}

// -- Vista Previa Actualizada --

@Preview(showBackground = true, name = "show")
@Composable
fun show() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        WinnerShareable(
            ganadorName = "Maciso",
            perderdorNamer = "Noob",
            ganadorRank = "Novato",
            perdedorRank = "Recluta",
            ganadorUrlImage = "https://firebasestorage.googleapis.com/v0/b/poolstreet-100jj001.firebasestorage.app/o/Perfiles%2FrZOliT7Jy0z450iuIluO.webp?alt=media&token=2c971120-b06c-4f55-b8fd-fa86085753e3", // "https://example.com/ganador.jpg",
            perdedorUrlImagen = null // "https://example.com/perdedor.jpg"
        )
    }
}