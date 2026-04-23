package pe.breaker.dkaviplay.util

import android.content.Intent
import androidx.core.net.toUri

actual fun openUrl(url: String?) {
    if (url.isNullOrBlank()) return

    try {
        val context = ContextProvider.getContext()
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        println("Error abriendo URL: ${e.message}")
    }
}