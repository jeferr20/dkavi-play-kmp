package pe.breaker.dkaviplay.util

import androidx.compose.ui.platform.UriHandler
import pe.breaker.dkaviplay.presentation.util.ToastHandler

fun handleAction(uriHandler: UriHandler, toast: ToastHandler, uri: String, errorMsg: String) {
    try {
        uriHandler.openUri(uri)
    } catch (e: Exception) {
        toast.showToast("$errorMsg: ${e.message}")
    }
}