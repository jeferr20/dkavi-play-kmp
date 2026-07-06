package pe.breaker.dkaviplay.presentation.screen.retoIniciado.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.button.CustomOutlineButtonTextIcon
import pe.breaker.dkaviplay.presentation.shareable.shareQRShareable.CaptureShareQRShareable
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.ShareHandler
import qrgenerator.QRCodeImage

@Composable
fun PartidaQrDialog(
    player1: String,
    player2: String,
    qrText: String?,
    onDismiss: () -> Unit,
){
    val shareHandler = koinInject<ShareHandler>()
    var triggerCapture by remember { mutableStateOf(false) }

    if (triggerCapture){
        CaptureShareQRShareable(
            qrText = qrText ?: "",
            user1 = player1,
            user2 = player2,
            onCaptured = { bitmap ->
                shareHandler.shareBitmap(bitmap)
                triggerCapture = false
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1C1E)),
            border = BorderStroke(1.dp, colorPrimary.copy(alpha = 0.25f)),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registrar resultado",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Comparte este QR con un árbitro o jugador externo para validar la partida.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerNameLabel(player1, Modifier.weight(1f))
                    Text(
                        text = "VS",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = colorPrimary
                    )
                    PlayerNameLabel(player2, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!qrText.isNullOrEmpty()) {
                        QRCodeImage(
                            modifier = Modifier.fillMaxSize(),
                            url = qrText,
                            contentDescription = "Código QR de la partida",
                        )
                    } else {
                        ErrorGeneratingQr()
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pasos de Instrucción
                InstructionSteps()

                Spacer(modifier = Modifier.height(12.dp))

                // Actions
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomOutlineButtonTextIcon(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colorOutline = colorPrimary,
                        icon = Icons.Default.Share,
                        enabled = !qrText.isNullOrEmpty() && !triggerCapture,
                        onClick = {
                            if (!qrText.isNullOrEmpty() && !triggerCapture) {
                                triggerCapture = true
                            }
                        },
                        text = "COMPARTIR QR"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CustomButtonFilled(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = true,
                        onClick = onDismiss,
                        text = "ENTENDIDO"
                    )
                }
            }
        }
    }
}