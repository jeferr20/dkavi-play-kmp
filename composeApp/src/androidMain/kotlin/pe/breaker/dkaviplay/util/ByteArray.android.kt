package pe.breaker.dkaviplay.util

import dev.gitlive.firebase.storage.Data

actual fun ByteArray.toFirebaseData(): Data {
    return Data(this)
}