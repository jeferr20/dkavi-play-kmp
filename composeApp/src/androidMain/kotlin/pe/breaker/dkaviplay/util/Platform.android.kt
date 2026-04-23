package pe.breaker.dkaviplay.util

import kotlin.system.exitProcess

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val isAndroid: Boolean = true
    override val isIos: Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun closeApp() {
    val context = ContextProvider.getContext()
    val activity = context.findActivity()
    if (activity != null) {
        activity.finishAffinity()
    } else {
        exitProcess(0)
    }
}