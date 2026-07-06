package pe.breaker.dkaviplay.core.util

import dev.gitlive.firebase.storage.Data

actual fun ByteArray.toFirebaseData(): Data {
    return Data(this)
}