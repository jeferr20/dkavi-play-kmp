package pe.breaker.dkaviplay.presentation.shareable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.ItemResourceMapper

@Composable
fun ItemObtainedShareable(
    userName: String,
    userUrlImage: String?,
    itemName: String?,
    itemId: Int?,
    modifier: Modifier = Modifier
) {
    val itemRes = ItemResourceMapper.getItemImage(itemId?: 0)

    val itemGlowGradient = Brush.radialGradient(
        colors = listOf(colorPrimary.copy(alpha = 0.3f), Color.Transparent),
        radius = 500f
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
            // Capa de brillo central
            Box(modifier = Modifier.fillMaxSize().background(itemGlowGradient))

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // --- HEADER: USUARIO ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = userUrlImage,
                            contentDescription = userName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(CircleShape)
                                .border(1.dp, colorPrimary, CircleShape)
                        )
                        if (userUrlImage == null) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(45.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // --- CENTRAL: EL ÍTEM ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¡NUEVO ÍTEM!",
                        color = colorPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Representación del Ítem con resplandor
                    Box(contentAlignment = Alignment.Center) {
                        // Halo de luz neón
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(colorPrimary.copy(alpha = 0.1f), CircleShape)
                                .blur(40.dp)
                        )

                        Image(
                            painter = painterResource(itemRes),
                            contentDescription = itemName,
                            modifier = Modifier
                                .size(200.dp) // Tamaño generoso para el ítem
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = itemName?.uppercase() ?: "",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .background(colorPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "DESBLOQUEADO",
                            color = colorPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // --- FOOTER ---
                Text(
                    text = "DKAVI PLAY",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 4.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemObtainedPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        ItemObtainedShareable(
            userName = "Maciso",
            userUrlImage = null,
            itemName = "Taco de Fuego",
            itemId = 1
        )
    }
}