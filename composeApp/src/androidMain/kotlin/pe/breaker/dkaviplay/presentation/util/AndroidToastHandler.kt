package pe.breaker.dkaviplay.presentation.util

import android.content.Context
import android.widget.Toast

class AndroidToastHandler(private val context: Context) : ToastHandler {
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}