package pe.breaker.dkaviplay.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AndroidImageResizer(private val context: Context) : ImageResizer {
    override suspend fun compressAndResize(uri: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try{
                val contentUri = uri.toUri()
                val inputStream = context.contentResolver.openInputStream(contentUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val outputStream = ByteArrayOutputStream()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 80, outputStream)
                } else {
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 80, outputStream)
                }
                outputStream.toByteArray()
            }catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}