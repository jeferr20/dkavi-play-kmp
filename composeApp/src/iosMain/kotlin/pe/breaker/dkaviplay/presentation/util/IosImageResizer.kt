package pe.breaker.dkaviplay.presentation.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

class IosImageResizer : ImageResizer {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun compressAndResize(uri: String): ByteArray? {
        val image = UIImage.imageWithContentsOfFile(uri) ?: return null

        // iOS no escribe WebP de forma nativa fácilmente (UIImageWriteToSavedPhotosAlbum no ayuda aquí)
        // Lo común es convertir a JPEG con compresión alta para simular el ahorro de WebP
        val data = UIImageJPEGRepresentation(image, 0.8) ?: return null

        val bytes = ByteArray(data.length.toInt())
        memcpy(bytes.refTo(0), data.bytes, data.length)
        return bytes
    }
}