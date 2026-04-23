package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dkaviplay.composeapp.generated.resources.Res
import dkaviplay.composeapp.generated.resources.ic_no_item_available
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.domain.model.inventory.MomentoUso
import pe.breaker.dkaviplay.domain.model.inventory.TipoPremio
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun InventarioSheetContent(
    items: List<DetallePremio>,
    isLoading: Boolean,
    onItemConfirm: (DetallePremio) -> Unit
) {
    // Definimos el ítem predeterminado (ID -1 suele ser buena práctica para "Ninguno")
    val itemPredeterminado = remember {
        DetallePremio(
            id = -1,
            nombre = "Sin ventaja",
            descripcion = "Jugar con equipamiento básico",
            tipo = TipoPremio.ITEM_JUGABLE,
            uso = MomentoUso.ANTES_PARTIDA,
            cantidad = 1,
            imagen = Res.drawable.ic_no_item_available
        )
    }

    // Al iniciar, si no hay selección, el predeterminado es el Rey
    var itemSeleccionado by remember { mutableStateOf<DetallePremio?>(itemPredeterminado) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 8.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EQUIPAMIENTO", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text("Selecciona una ventaja para tu partida", color = Color.Gray, fontSize = 13.sp)

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = colorPrimary, modifier = Modifier.padding(40.dp))
        } else {
            val itemsFiltrados = items.filter { it.cantidad > 0 }

            if (itemsFiltrados.isEmpty()) {
                // --- CASO: NO TIENE ÍTEMS ---
                // Mostramos solo el predeterminado resaltado
                ItemJugableCard(
                    premio = itemPredeterminado,
                    isSelected = true, // Siempre seleccionado si es el único
                    onClick = { itemSeleccionado = itemPredeterminado }
                )
            } else {
                // --- CASO: TIENE ÍTEMS ---
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Opcional: Podrías agregar el itemPredeterminado al inicio de la lista
                    // para que siempre puedan elegir "No usar nada"
                    item(key = "default") {
                        ItemJugableCard(
                            premio = itemPredeterminado,
                            isSelected = itemSeleccionado?.id == -1,
                            onClick = { itemSeleccionado = itemPredeterminado }
                        )
                    }

                    items(itemsFiltrados, key = { it.id }) { premio ->
                        ItemJugableCard(
                            premio = premio,
                            isSelected = itemSeleccionado?.id == premio.id,
                            onClick = { itemSeleccionado = premio }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Box(modifier = Modifier.padding(horizontal = 24.dp)) {
            CustomButtonFilled(
                onClick = { itemSeleccionado?.let { onItemConfirm(it) } },
                enabled = itemSeleccionado != null,
                text = if (itemSeleccionado?.id == -1) "Jugar sin ítem" else "Usar ítem",
            )
        }
    }
}