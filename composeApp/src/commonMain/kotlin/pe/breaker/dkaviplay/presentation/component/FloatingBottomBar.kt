package pe.breaker.dkaviplay.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab

@Composable
fun FloatingBottomBar(
    tabs: List<Tab>
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp) // Margen para que flote
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp), // Bordes muy redondeados
        color = Color(0xFF1A1A1A), // Un gris muy oscuro para que resalte del fondo negro
        tonalElevation = 8.dp,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                FloatingTabItem(tab)
            }
        }
    }
}