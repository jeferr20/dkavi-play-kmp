package pe.breaker.dkaviplay.presentation.screen.registerReserva.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun GameSelect(
    modifier: Modifier,
    imagen: DrawableResource,
    titulo: String,
    juegos: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(220.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 0.5.dp,
            color = if (selected) colorPrimary else colorPrimary.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(if (selected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF1E293B) else Color(0xFF111827)
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(imagen),
                contentDescription = titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$juegos Juego(s)",
                        color = Color(0xFF90a1b9),
                        fontSize = 14.sp,
                        maxLines = 2
                    )
                }

                RadioButton(
                    selected = selected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = colorPrimary,
                        unselectedColor = Color(0xFF475569)
                    )
                )
            }
        }
    }
}