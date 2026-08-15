package pe.breaker.dkaviplay.presentation.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.content.Context
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidImageResizer(private val context: Context) : ImageResizer {

    override suspend fun compressAndResize(uriString: String): ByteArray? {
        val uri = uriString.toUri()

        // 1. Obtener la orientación EXIF
        val orientation = getExifOrientation(uri)

        // 2. Decodificar la imagen
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
        withContext(Dispatchers.IO) {
            inputStream.close()
        }

        // 3. Aplicar rotación según la etiqueta EXIF
        val rotatedBitmap = rotateBitmapIfNeeded(originalBitmap, orientation)

        // 4. Redimensionar y comprimir a WebP / JPEG
        // ... tu lógica para escalar ...
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.WEBP, 80, outputStream)

        return outputStream.toByteArray()
    }

    private fun getExifOrientation(uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
            } ?: ExifInterface.ORIENTATION_UNDEFINED
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_UNDEFINED
        }
    }

    private fun rotateBitmapIfNeeded(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap // No requiere rotación
        }

        val rotated = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        if (rotated != bitmap) {
            bitmap.recycle() // Liberar memoria del bitmap original
        }
        return rotated
    }
}