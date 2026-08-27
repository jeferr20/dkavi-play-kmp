package pe.breaker.dkaviplay.util

interface Platform {
    val name: String
    val isAndroid: Boolean
    val isIos: Boolean
    val isSimulator: Boolean
}

expect fun getPlatform(): Platform

expect fun closeApp()