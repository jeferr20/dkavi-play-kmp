package pe.breaker.dkaviplay.presentation.screen.quickPlayDetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun CustomLabelInfo(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    color: Color
) {
    Card(
        modifier = modifier,
        border = BorderStroke(0.5.dp, colorPrimary.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1d293d))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                color = Color(0xFF90a1b9),
                fontSize = 16.sp
            )
            Text(
                text = text,
                color = color,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}