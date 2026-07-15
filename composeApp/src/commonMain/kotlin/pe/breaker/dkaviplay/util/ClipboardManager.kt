package pe.breaker.dkaviplay.util

expect object ClipboardManager {
    fun copyToClipboard(text: String)
    fun getFromClipboard(): String?
}
