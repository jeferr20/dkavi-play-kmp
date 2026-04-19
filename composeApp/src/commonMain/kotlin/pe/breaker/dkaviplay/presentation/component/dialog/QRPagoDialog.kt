package pe.breaker.dkaviplay.presentation.component.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import pe.breaker.dkaviplay.presentation.component.button.CustomButtonFilled

@Composable
fun QRPagoDialog(
    onDismissRequest: () -> Unit,
    monto: Double,
    text: String
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(28.dp),
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF0F1113),
        title = null,
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título y Monto
                Text(
                    text = "Pagar Reserva",
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "S/ $monto",
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tarjeta del QR
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.size(260.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.QR_Pago),
                        contentDescription = "QR de Pago",
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Instrucción
                Text(
                    text = text,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            CustomButtonFilled(
                enabled = true,
                onClick = onDismissRequest,
                text = "ENTENDIDO"
            )
        }
    )
}