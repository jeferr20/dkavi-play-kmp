package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.ItemResourceMapper

@Composable
fun ItemJugableCard(
    premio: DetallePremio,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) colorPrimary else Color.White.copy(alpha = 0.1f)
    val backgroundColor = if (isSelected) colorPrimary.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)
    val esSinItem = premio.cantidad < 1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (esSinItem) Arrangement.Center else Arrangement.Top,
        modifier = Modifier
            .width(110.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(ItemResourceMapper.getItemImage(premio.id)),
            contentDescription = premio.nombre,
            modifier = Modifier.size(50.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = premio.nombre,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = if (esSinItem) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().weight(1f)
        )

        if (!esSinItem) {
            Text(
                text = "Disp: ${premio.cantidad}",
                color = if (isSelected) Color.White else colorPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}