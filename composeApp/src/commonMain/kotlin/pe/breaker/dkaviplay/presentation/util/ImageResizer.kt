package pe.breaker.dkaviplay.presentation.util

interface ImageResizer {
    suspend fun compressAndResize(uriString: String): ByteArray?
}