package pe.breaker.dkaviplay.util

import android.os.Build
import kotlin.system.exitProcess

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val isAndroid: Boolean = true
    override val isIos: Boolean = false
    override val isSimulator: Boolean = Build.PRODUCT.contains("sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86")
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