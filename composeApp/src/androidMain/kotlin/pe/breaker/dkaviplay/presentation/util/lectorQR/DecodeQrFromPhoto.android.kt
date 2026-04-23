package pe.breaker.dkaviplay.presentation.util.lectorQR

import android.content.Context
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import androidx.core.net.toUri
import com.google.mlkit.vision.common.InputImage

actual suspend fun decodeQrFromPhoto(photo: GalleryPhotoResult, context: Any?): String? = suspendCancellableCoroutine { continuation ->
    try{
        val androidContext = context as Context
        val uriString = photo.uri
        val uri = uriString.toUri()

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val scanner = BarcodeScanning.getClient(options)

        val image = InputImage.fromFilePath(androidContext, uri)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Retornamos el valor del primer QR encontrado
                val qrValue = barcodes.firstOrNull()?.rawValue
                continuation.resume(qrValue)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                continuation.resume(null)
            }
            .addOnCompleteListener {
                scanner.close()
            }
    }catch (e: Exception){
        continuation.resume(null)
        e.printStackTrace()
    }
}