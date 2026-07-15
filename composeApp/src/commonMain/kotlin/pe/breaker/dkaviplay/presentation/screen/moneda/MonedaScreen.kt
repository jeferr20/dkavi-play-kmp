package pe.breaker.dkaviplay.presentation.screen.moneda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.QRPagoDialog
import pe.breaker.dkaviplay.presentation.screen.moneda.components.CustomCoinCounter
import pe.breaker.dkaviplay.presentation.screen.moneda.components.InfoMonedasDialog
import pe.breaker.dkaviplay.presentation.screen.moneda.components.QuickBundleGrid
import pe.breaker.dkaviplay.presentation.util.ToastHandler
import pe.breaker.dkaviplay.util.ClipboardManager
import pe.breaker.dkaviplay.util.handleAction

class MonedaScreen : Screen {
    @Composable
    override fun Content() {
        val toastHandler = koinInject<ToastHandler>()
        val model = koinScreenModel<MonedaModel>()
        val uriHandler = LocalUriHandler.current

        val state by model.state.collectAsState()
        var cantidadMonedas by remember { mutableStateOf(100) }
        val navigator = LocalNavigator.currentOrThrow
        val paquetesSugeridos = remember { listOf(100, 200, 500, 1000, 2000, 5000) }
        val esMontoValido = cantidadMonedas > 0

        var showInfoDialog by remember { mutableStateOf(false) }

        LaunchedEffect(state.actionEvent) {
            when (val event = state.actionEvent) {
                is MonedaUiEvent.OpenWhatsApp -> {
                    handleAction(uriHandler, toastHandler, event.url, "No se pudo abrir WhatsApp")
                    model.consumeActionEvent()
                    navigator.pop()
                }

                null -> {}
            }
        }

        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let {
                toastHandler.showToast(it)
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier.imePadding(),
            containerColor = Color.Black
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        CustomAppbar(onClick = { navigator.pop() }, text = "Conseguir monedas")
                    }

                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Saber más sobre monedas",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                CustomCoinCounter(
                    cantidad = cantidadMonedas,
                    onCantidadChanged = { nuevaCantidad -> cantidadMonedas = nuevaCantidad }
                )

                Spacer(modifier = Modifier.height(24.dp))

                QuickBundleGrid(
                    paquetes = paquetesSugeridos,
                    cantidadSeleccionada = cantidadMonedas,
                    onBundleSelected = { cantidad -> cantidadMonedas = cantidad },
                    modifier = Modifier.weight(1f)
                )

                CustomButtonFilled(
                    text = if (esMontoValido) "COMPRAR $cantidadMonedas MONEDAS" else "INGRESE UNA CIDAD",
                    onClick = {
                        if (esMontoValido) {
                            model.procesarCompra(cantidadMonedas)
                        }
                    },
                    enabled = state.isTiendaDisponible && esMontoValido && !state.isLoading,
                    icon = Icons.Default.MonetizationOn,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (showInfoDialog) {
                InfoMonedasDialog(
                    precioMoneda = state.precioMoneda,
                    monedasBase = paquetesSugeridos[0],
                    onDismiss = { showInfoDialog = false }
                )
            }

            if (state.showPagoDialog) {
                QRPagoDialog(
                    monto = state.montoCalculado,
                    text = "Escanea el QR de la sede para pagar vía Yape/Plin o copia el número de celular. Al terminar, presiona el botón inferior para reportarlo por WhatsApp al administrador.",
                    numCelularPago = state.numPago ?: "",
                    onCopiarNumero = { numero ->
                        ClipboardManager.copyToClipboard(numero)
                        toastHandler.showToast("Número copiado al portapapeles")
                    },
                    onEnviarWhatsApp = {
                        model.enviarConfirmacionWhatsApp()
                    },
                    onDismissRequest = {
                        model.ocultarPagoDialog()
                    }
                )
            }
            if (state.isLoading) {
                LoadingDialog()
            }
        }
    }
}