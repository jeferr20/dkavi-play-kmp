package pe.breaker.dkaviplay.util

import platform.UIKit.UIDevice
import platform.posix.exit

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val isAndroid: Boolean = false
    override val isIos: Boolean = true
    override val isSimulator: Boolean by lazy {
        val name = UIDevice.currentDevice.name
        name.contains("Simulator", ignoreCase = true)
    }
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun closeApp() {exit(0) }