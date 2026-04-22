package pe.breaker.dkaviplay.presentation.screen.inventario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.domain.model.inventory.TipoPremio
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.screen.inventario.components.DetalleItemDialog
import pe.breaker.dkaviplay.presentation.screen.inventario.components.ItemCard

class InventarioScreen(val tipoItem: TipoPremio) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<InventarioModel>(
            parameters = { parametersOf(tipoItem) }
        )
        val state by model.state.collectAsState()
        var itemSeleccionado by remember { mutableStateOf<DetallePremio?>(null) }
        val (titulo, descripcion) = getCabeceraInfo(tipoItem)

        Scaffold(
            containerColor = Color.Black,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                CustomAppbar(onClick = { navigator.pop() })

                Spacer(modifier = Modifier.height(16.dp))

                // Cabecera de la sección
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Cuadrícula de ítems
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 columnas para que luzcan los detalles
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.items) { item ->
                        ItemCard(
                            item = item,
                            onClick = { itemSeleccionado = item })
                    }
                }
            }

            itemSeleccionado?.let { premio ->
                DetalleItemDialog(
                    premio = premio,
                    onDismiss = { itemSeleccionado = null },
                    onConfirm = {
                        itemSeleccionado = null
                    }
                )
            }
        }
    }

    private fun getCabeceraInfo(tipo: TipoPremio): Pair<String, String> {
        return when (tipo) {
            TipoPremio.ITEM_JUGABLE ->
                "MIS PODERES" to "Usa estos ítems durante tus partidas para ganar ventaja."
            TipoPremio.PREMIO_FISICO ->
                "MIS PREMIOS" to "Aquí están tus recompensas físicas obtenidas en el club."
            TipoPremio.MONETARIO ->
                "MIS CUPONES" to "Canjea estos descuentos en tus próximos consumos o mesas."
            TipoPremio.RETO ->
                "MIS RETOS" to "Desafíos especiales que puedes activar cuando quieras."
        }
    }
}