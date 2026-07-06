package pe.breaker.dkaviplay.presentation.screen.moneda.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.breaker.dkaviplay.presentation.theme.goldColor

@Composable
fun QuickBundleGrid(
    paquetes: List<Int>,
    cantidadSeleccionada: Int,
    onBundleSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Selección rápida",
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(paquetes) { cantidad ->
                val isSelected = cantidadSeleccionada == cantidad
                val cardBorderColor by animateColorAsState(
                    if (isSelected) goldColor else Color.White.copy(alpha = 0.05f)
                )
                val cardBgColor by animateColorAsState(
                    if (isSelected) goldColor.copy(alpha = 0.1f) else Color(0xFF111111)
                )

                Box(
                    modifier = Modifier
                        .height(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBgColor)
                        .border(1.dp, cardBorderColor, RoundedCornerShape(16.dp))
                        .clickable { onBundleSelected(cantidad) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = if (isSelected) goldColor else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = cantidad.toString(),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}