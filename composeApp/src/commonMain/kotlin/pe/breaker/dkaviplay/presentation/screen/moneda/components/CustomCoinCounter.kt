package pe.breaker.dkaviplay.presentation.screen.moneda.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.goldColor

@Composable
fun CustomCoinCounter(
    cantidad: Int,
    onCantidadChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF111111),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Cantidad seleccionada",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        if (cantidad >= 50) onCantidadChanged(cantidad - 50) else onCantidadChanged(0)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF1E1E1E), CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Restar", tint = Color.White)
                }

                Text(
                    text = cantidad.toString(),
                    color = goldColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(150.dp)
                        .padding(horizontal = 12.dp)
                )

                IconButton(
                    onClick = { onCantidadChanged(cantidad + 50) },
                    modifier = Modifier
                        .size(44.dp)
                        .background(goldColor, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Sumar", tint = Color.Black)
                }
            }
        }
    }
}