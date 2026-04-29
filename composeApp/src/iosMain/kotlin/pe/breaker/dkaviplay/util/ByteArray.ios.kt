package pe.breaker.dkaviplay.util

import dev.gitlive.firebase.storage.Data
import kotlinx.cinterop.*
import platform.Foundation.NSData
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun ByteArray.toFirebaseData(): Data {
    val nsData = this.usePinned {
        NSData.create(bytes = it.addressOf(0), length = size.toULong())
    }
    return Data(nsData)
}