package pe.breaker.dkaviplay.presentation.screen.inventario.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.ItemResourceMapper

@Composable
fun DetalleItemDialog(
    premio: DetallePremio,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1231)),
            border = BorderStroke(1.dp, colorPrimary.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Imagen en grande
                Image(
                    painter = painterResource(ItemResourceMapper.getItemImage(premio.id)),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Título
                Text(
                    text = premio.nombre.uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // DESCRIPCIÓN (El campo .info que agregamos antes)
                Text(
                    text = premio.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón de Acción
//                Button(
//                    onClick = onConfirm,
//                    modifier = Modifier.fillMaxWidth().height(50.dp),
//                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Text(
//                        text = "USAR PODER",
//                        color = Color.Black,
//                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
//                    )
//                }

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("CERRAR", color = Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}