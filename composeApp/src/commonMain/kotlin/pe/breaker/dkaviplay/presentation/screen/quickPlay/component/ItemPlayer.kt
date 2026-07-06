package pe.breaker.dkaviplay.presentation.screen.quickPlay.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import pe.breaker.dkaviplay.core.domain.model.UserQuick
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun ItemPlayer(
    modifier: Modifier = Modifier,
    user: UserQuick,
    onChallengeClick: (UserQuick) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        border = BorderStroke(0.5.dp, colorPrimary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // FOTO PERFIL
            AsyncImage(
                model = user.imagen,
                contentDescription = "Foto jugador",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // INFO JUGADOR
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.usuario,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Rango: ${user.rango}",
                    color = colorPrimary,
                    fontSize = 13.sp
                )
            }

            CustomButtonFilled(
                modifier = Modifier.height(56.dp).padding(start = 8.dp),
                enabled = true,
                onClick = { onChallengeClick(user) },
                text = "Retar",
            )
        }
    }
}