package pe.breaker.dkaviplay.presentation.shareable.battleShareable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.shareable.DarkGrayColor
import pe.breaker.dkaviplay.presentation.shareable.LightGrayColor
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper

// Colores para el modo Batalla
//val BattleRed = Color(0xFFE91E63)
val BattleRed = Color(0x242424)
//val BattleBlue = Color(0xFF2196F3)
val BattleBlue = colorPrimary
val VersusGradient = Brush.horizontalGradient(
    colors = listOf(BattleBlue.copy(alpha = 0.5f), BattleRed.copy(alpha = 0.5f))
)

@Composable
fun BattleShareable(
    user1Name: String,
    user2Name: String,
    user1Rank: String,
    user2Rank: String,
    user1UrlImage: String?,
    user2UrlImage: String?,
    modifier: Modifier = Modifier
) {
    val rank1Res = RankResourceMapper.getDrawableByRank(user1Rank)
    val rank2Res = RankResourceMapper.getDrawableByRank(user2Rank)

    Card(
        modifier = modifier.size(350.dp, 550.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Fondo con sutil resplandor dividido
            Box(modifier = Modifier.fillMaxSize().background(VersusGradient))

            // Usamos una Box para separar el contenido centrado del footer
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween // Separa el header, el centro y el footer
            ) {
                // Header
                Text(
                    text = "NUEVO RETO",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )

                // --- CONTENEDOR CENTRAL CENTRADO ---
                // Esta columna interna se encarga de que los players y el VS estén pegados y centrados
                Column(
                    modifier = Modifier.weight(1f), // Ocupa el espacio disponible
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Centro vertical puro
                ) {
                    // --- USUARIO 1 ---
                    BattlePlayerInfo(
                        name = user1Name,
                        rank = user1Rank,
                        rankRes = rank1Res,
                        imageUrl = user1UrlImage,
                        borderColor = BattleBlue,
                        alignment = Alignment.Start
                    )

                    // --- DIVISOR VS ---
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp) // Espacio controlado entre usuarios
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Transparent, Color.White, Color.Transparent)
                                    )
                                )
                        )
                        Text(
                            text = "VS",
                            color = Color.White,
                            fontSize = 54.sp, // Un poco más grande para impacto
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier
                                .background(Color.Black)
                                .padding(horizontal = 16.dp)
                        )
                    }

                    // --- USUARIO 2 ---
                    BattlePlayerInfo(
                        name = user2Name,
                        rank = user2Rank,
                        rankRes = rank2Res,
                        imageUrl = user2UrlImage,
                        borderColor = BattleRed,
                        alignment = Alignment.End
                    )
                }

                // Footer (Siempre abajo)
                Text(
                    text = "DKAVI PLAY",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 4.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }
    }
}
@Composable
fun BattlePlayerInfo(
    name: String,
    rank: String,
    rankRes: DrawableResource, // <--- Cambiado de Int a DrawableResource
    imageUrl: String?,
    borderColor: Color,
    alignment: Alignment.Horizontal
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (alignment == Alignment.Start) Arrangement.Start else Arrangement.End
        ) {
            if (alignment == Alignment.End) {
                PlayerTextInfo(name, rank, rankRes, alignment)
                Spacer(modifier = Modifier.width(12.dp))
            }

            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(3.dp, borderColor, CircleShape)
                        .background(DarkGrayColor)
                )
                if (imageUrl == null) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(90.dp)
                    )
                }
            }

            if (alignment == Alignment.Start) {
                Spacer(modifier = Modifier.width(12.dp))
                PlayerTextInfo(name, rank, rankRes, alignment)
            }
        }
    }
}

@Composable
fun PlayerTextInfo(
    name: String,
    rank: String,
    rankRes: DrawableResource, // <--- Cambiado de Int a DrawableResource
    alignment: Alignment.Horizontal
) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = name.uppercase(),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (alignment == Alignment.End) {
                Text(text = rank, color = LightGrayColor, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
            }
            // Ahora painterResource aceptará el rankRes sin errores
            Image(
                painter = painterResource(rankRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            if (alignment == Alignment.Start) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = rank, color = LightGrayColor, fontSize = 14.sp)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun BattlePreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        BattleShareable(
            user1Name = "Maciso",
            user2Name = "Noob Master",
            user1Rank = "Recluta",
            user2Rank = "Novato",
            user1UrlImage = null,
            user2UrlImage = null
        )
    }
}