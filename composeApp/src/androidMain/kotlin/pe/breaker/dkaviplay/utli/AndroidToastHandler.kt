package pe.breaker.dkaviplay.utli

import android.content.Context
import android.widget.Toast
import pe.breaker.dkaviplay.util.ToastHandler

class AndroidToastHandler(private val context: Context) : ToastHandler {
    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}