package pe.breaker.dkaviplay.presentation.screen.aceptarReto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.button.CustomOutlineButtonTextIcon
import pe.breaker.dkaviplay.presentation.theme.colorRedError
import pe.breaker.dkaviplay.presentation.theme.colorWhatsapp
import pe.breaker.dkaviplay.presentation.util.ToastHandler

@Composable
fun AccionesReto(
    successMessage: String?,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClose: () -> Unit,
    onShowPaymentQr: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val toastHandler = koinInject<ToastHandler>()

    Box(contentAlignment = Alignment.Center) {
        if (successMessage != null) {
            val isWhatsAppLink = successMessage.startsWith("http")
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = (if (isWhatsAppLink) Color(0xFF22C55E) else Color(0xFFEF4444)).copy(
                        alpha = 0.15f
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().border(
                        1.dp,
                        if (isWhatsAppLink) Color(0xFF22C55E) else Color(0xFFEF4444),
                        RoundedCornerShape(16.dp)
                    )
                ) {
                    Text(
                        text = if (isWhatsAppLink) "¡Reto Aceptado!" else successMessage,
                        modifier = Modifier.padding(20.dp),
                        color = if (isWhatsAppLink) Color(0xFF22C55E) else Color(0xFFEF4444),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isWhatsAppLink) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Botón QR (Nuevo)
                        OutlinedButton(
                            onClick = onShowPaymentQr,
                            modifier = Modifier.weight(1f).height(58.dp),
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                Icons.Rounded.QrCode2,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "QR PAGO",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        // Botón WhatsApp
                        CustomButtonFilled(
                            modifier = Modifier.weight(1f).height(56.dp),
                            enabled = true,
                            colorBackGround = colorWhatsapp,
                            onClick = {
                                openWhatsApp(successMessage, uriHandler, toastHandler)
                                onClose()
                            },
                            icon = Icons.Rounded.Payments,
                            text = "WHATSAPP"
                        )
                    }
                } else {
                    Button(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF333333),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("CERRAR", fontWeight = FontWeight.Black)
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CustomOutlineButtonTextIcon(
                    modifier = Modifier.weight(1f).height(56.dp),
                    onClick = onReject,
                    colorOutline = colorRedError,
                    enabled = true,
                    text = "PASO"
                )

                CustomButtonFilled(
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = true,
                    onClick = onAccept,
                    text = "¡ACEPTO!"
                )
            }
        }
    }
}

private fun openWhatsApp(
    mensajeExito: String?,
    uriHandler: UriHandler,
    toastHandler: ToastHandler
) {
    try {
        mensajeExito?.let {
            uriHandler.openUri(it)
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
        toastHandler.showToast("No se pudo abrir WhatsApp")
    }
}