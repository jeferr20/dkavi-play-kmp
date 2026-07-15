package pe.breaker.dkaviplay.presentation.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import dkaviplay.composeapp.generated.resources.QR_Pago
import dkaviplay.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
@Composable
fun QRPagoDialog(
    monto: Double,
    text: String,
    numCelularPago: String,
    onCopiarNumero: (String) -> Unit,
    onEnviarWhatsApp: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val montoFormateado = "S/. ${((monto * 100).toInt() / 100.0)}"

    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(28.dp),
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF0F1113),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pagar Compra",
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = montoFormateado,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.size(240.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.QR_Pago),
                        contentDescription = "QR de Pago",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Acción de Copiar Número (Manejado por la plataforma)
                TextButton(
                    onClick = { onCopiarNumero(numCelularPago) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copiar número ($numCelularPago)",
                        color = Color(0xFF2196F3),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Instrucción Dinámica
                Text(
                    text = text,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Principal: Ejecuta la acción nativa de WhatsApp
                CustomButtonFilled(
                    enabled = true,
                    onClick = {
                        onEnviarWhatsApp()
                        onDismissRequest() // Cierra tras lanzar la acción
                    },
                    text = "YA PAGUÉ, ENVIAR A WHATSAPP"
                )

                // Botón Secundario
                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CANCELAR",
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    )
}