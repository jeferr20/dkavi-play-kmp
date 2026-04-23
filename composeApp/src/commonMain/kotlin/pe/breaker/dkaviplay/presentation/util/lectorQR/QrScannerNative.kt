package pe.breaker.dkaviplay.presentation.util.lectorQR

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun QrScannerNative(
    modifier: Modifier,
    onQrDetected: (String) -> Unit,
    onReady: () -> Unit,
    onFailure: (String) -> Unit
)