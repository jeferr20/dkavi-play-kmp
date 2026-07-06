package pe.breaker.dkaviplay.presentation.screen.perfil.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper

@Composable
fun ProfileHeader(
    modifier: Modifier,
    nombre: String,
    rango: String,
    monedas: Int,
    urlImage: String?,
    onConseguirMonedas: () -> Unit,
    onImagenClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1C1E))
                    .border(2.dp, colorPrimary, CircleShape)
                    .clickable { onImagenClick() },
                contentAlignment = Alignment.Center
            ) {
                if (!urlImage.isNullOrBlank()) {
                    AsyncImage(
                        model = urlImage,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .offset(x = (-4).dp, y = (-4).dp),
                shape = CircleShape,
                color = Color(0xFF0D1231),
                border = BorderStroke(2.dp, colorPrimary),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(RankResourceMapper.getDrawableByRank(rango)),
                        contentDescription = "Rango Icon",
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = nombre.uppercase(),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
        )

        Row(
            modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RangoBadge(rango = rango)

            CoinBalanceBadge(
                coins = monedas,
                onClick = onConseguirMonedas,
            )
        }
    }
}