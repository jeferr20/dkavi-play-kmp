package pe.breaker.dkaviplay.presentation.util.lectorQR

import android.annotation.SuppressLint
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@Composable
actual fun QrScannerNative(
    modifier: Modifier,
    onQrDetected: (String) -> Unit,
    onReady: () -> Unit,
    onFailure: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember(context) { ContextCompat.getMainExecutor(context) }

    // 1. Flag para evitar lecturas duplicadas (Debounce)
    var isDetected by remember { mutableStateOf(false) }

    // 2. Configuración PRO de ML Kit (Solo QR para ahorrar CPU)
    val barcodeScanner = remember {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        BarcodeScanning.getClient(options)
    }

    val cameraProviderFuture = remember(context) { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(Unit) {
        isDetected = false
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3) // Estándar de cámara
                        .build()
                        .also { it.surfaceProvider = previewView.surfaceProvider }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        if (isDetected) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        processImagePro(
                            barcodeScanner,
                            imageProxy,
                            onDetected = { qr ->
                                if (!isDetected) {
                                    isDetected = true
                                    onQrDetected(qr)
                                }
                            }
                        )
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )

                    previewView.post { onReady() }
                    // 3. Habilitar Auto-foco si el hardware lo permite
                    camera.cameraControl.enableTorch(false) // Podemos exponer esto luego

                } catch (e: Exception) {
                    onFailure("Error Cámara: ${e.localizedMessage}")
                }
            }, executor)

            previewView
        },
        modifier = modifier,
        onRelease = {
            cameraProviderFuture.get().unbindAll()
            barcodeScanner.close()
        }
    )
}

@SuppressLint("UnsafeOptInUsageError")
private fun processImagePro(
    scanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image ?: run {
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let { qr ->
                onDetected(qr)
            }
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}