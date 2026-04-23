package pe.breaker.dkaviplay.presentation.util.lectorQR

import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import platform.CoreImage.CIDetector
import platform.CoreImage.CIDetectorTypeQRCode
import platform.CoreImage.CIImage
import platform.Foundation.NSURL

actual suspend fun decodeQrFromPhoto(
    photo: GalleryPhotoResult,
    context: Any?
): String? {
    val uriString = photo.uri ?: return null
    val url = NSURL.URLWithString(uriString) ?: return null
    val image = CIImage.imageWithContentsOfURL(url) ?: return null

    val detector = CIDetector.detectorOfType(
        type = CIDetectorTypeQRCode,
        context = null,
        options = null
    )

    val features = detector?.featuresInImage(image)
    // Retornamos el mensaje del primer QR encontrado
    return (features?.firstOrNull() as? platform.CoreImage.CIQRCodeFeature)?.messageString
}