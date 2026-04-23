package pe.breaker.dkaviplay.presentation.util.lectorQR

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.AVFoundation.*
import platform.UIKit.*
import kotlinx.cinterop.*
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun QrScannerNative(
    modifier: Modifier,
    onQrDetected: (String) -> Unit,
    onReady: () -> Unit,
    onFailure: (String) -> Unit
) {
    // 1. Flag de detección para evitar lecturas múltiples (Debounce)
    var isDetected by remember { mutableStateOf(false) }

    UIKitView(
        factory = {
            val container = UIView()
            val captureSession = AVCaptureSession()

            // Intentar obtener la cámara trasera por defecto
            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (device == null) {
                onFailure("No se detectó cámara o no hay permisos")
                return@UIKitView container
            }

            // Configuración de Entrada
            val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null) as? AVCaptureDeviceInput
            if (input != null && captureSession.canAddInput(input)) {
                captureSession.addInput(input)
            } else {
                onFailure("No se pudo acceder a la entrada de cámara")
            }

            // Configuración de Salida de Metadatos (Escáner QR)
            val metadataOutput = AVCaptureMetadataOutput()
            if (captureSession.canAddOutput(metadataOutput)) {
                captureSession.addOutput(metadataOutput)

                metadataOutput.setMetadataObjectsDelegate(object : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
                    override fun captureOutput(
                        output: AVCaptureOutput,
                        didOutputMetadataObjects: List<*>,
                        fromConnection: AVCaptureConnection
                    ) {
                        if (isDetected) return // Si ya detectamos uno, ignoramos el resto

                        val metadataObject = didOutputMetadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject

                        if (metadataObject?.type == AVMetadataObjectTypeQRCode) {
                            val qrValue = metadataObject?.stringValue
                            if (qrValue != null) {
                                isDetected = true // Bloqueamos nuevas detecciones
                                onQrDetected(qrValue)
                            }
                        }
                    }
                }, queue = dispatch_get_main_queue())

                // Solo nos interesan los códigos QR
                metadataOutput.metadataObjectTypes = listOf(AVMetadataObjectTypeQRCode)
            }

            // Capa de Previsualización (Similar a PreviewView de Android)
            val previewLayer = AVCaptureVideoPreviewLayer.layerWithSession(captureSession).apply {
                videoGravity = AVLayerVideoGravityResizeAspectFill
                // Es vital que el frame se asigne correctamente
                frame = container.bounds
            }
            container.layer.addSublayer(previewLayer)

            // Iniciamos la sesión en un hilo global para no congelar la UI
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0u)) {
                captureSession.startRunning()
                // Avisamos que la cámara está lista
                dispatch_async(dispatch_get_main_queue()) {
                    onReady()
                }
            }

            container
        },
        modifier = modifier,
        update = { view ->
            // Sincronizar el tamaño del PreviewLayer con el tamaño del Composable
            val layer = view.layer.sublayers?.firstOrNull() as? AVCaptureVideoPreviewLayer
            layer?.frame = view.bounds
        }
    )
}