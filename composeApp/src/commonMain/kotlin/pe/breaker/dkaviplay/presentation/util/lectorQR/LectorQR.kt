package pe.breaker.dkaviplay.presentation.util.lectorQR

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.util.lectorQR.components.GalleryFrame
import pe.breaker.dkaviplay.presentation.util.lectorQR.components.ScannerFrame
import pe.breaker.dkaviplay.presentation.util.lectorQR.components.TextFrame

@Composable
fun LectorQR(
    modifier: Modifier = Modifier,
    onQrDetected: (String) -> Unit,
    label: String? = null,
    context: Any? = null
) {
    var isCameraReady by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. LLAMADA AL COMPONENTE NATIVO (Expect/Actual)
        // Usamos fillMaxSize para que el PreviewView nativo controle su AspectRatio
        QrScannerNative(
            modifier = Modifier.fillMaxSize(),
            onQrDetected = { qrCode ->
                if (qrCode.isNotEmpty()) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onQrDetected(qrCode)
                }
            },
            onReady = { isCameraReady = true },
            onFailure = { error ->
                println("Error nativo de cámara: $error")
            }
        )

        // 2. MÁSCARA DE ENFOQUE (HUD)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val scanBoxSize = size.width * 0.7f
                    val left = (size.width - scanBoxSize) / 2
                    val top = (size.height - scanBoxSize) / 2
                    val rect = Rect(left, top, left + scanBoxSize, top + scanBoxSize)

                    val rectPath = Path().apply { addRect(Rect(0f, 0f, size.width, size.height)) }
                    val holePath = Path().apply {
                        addRoundRect(RoundRect(rect, CornerRadius(24.dp.toPx())))
                    }
                    val finalPath = Path.combine(PathOperation.Difference, rectPath, holePath)

                    drawPath(finalPath, color = Color.Black.copy(alpha = 0.75f))
                },
            contentAlignment = Alignment.Center
        ) {
            // El visor (ScannerFrame) coincidiendo al 100% con el hueco
            ScannerFrame(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
            )
        }

        GalleryFrame(
            onGalleryClick = { showGallery = true }
        )

        if(showGallery){
            GalleryPickerLauncher(
                includeExif = true,
                onPhotosSelected = { photos ->
                    photos.firstOrNull()?.let { photo ->
                        scope.launch {
                            val qrCode = decodeQrFromPhoto(photo, context)
                            if(qrCode != null){
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onQrDetected(qrCode)
                            }else {
                                println("No se encontró un QR válido en la imagen")
                            }
                        }
                    }
                    showGallery = false
                },
                onError = { showGallery = false },
                onDismiss = { showGallery = false },
                allowMultiple = false
            )
        }

        // 3. INDICACIONES
        label?.let {
            TextFrame(label = label)
        }

        AnimatedVisibility(visible = !isCameraReady, exit = fadeOut(tween(500))) {
            LoadingDialog()
        }
    }
}
