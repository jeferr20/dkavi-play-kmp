package pe.breaker.dkaviplay.util

import android.content.ClipData
import android.content.Context
import android.content.ClipboardManager as AndroidClipboardManager

actual object ClipboardManager {
    // Necesitamos guardar una referencia al contexto de la app.
    // Asegúrate de inicializar esto en tu MainActivity o Application class.
    lateinit var applicationContext: Context

    private val clipboard: AndroidClipboardManager?
        get() = if (::applicationContext.isInitialized) {
            applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager
        } else {
            null
        }

    actual fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText("copied_text", text)
        clipboard?.setPrimaryClip(clip)
    }

    actual fun getFromClipboard(): String? {
        val clip = clipboard?.primaryClip
        if (clip != null && clip.itemCount > 0) {
            return clip.getItemAt(0).text?.toString()
        }
        return null
    }
}