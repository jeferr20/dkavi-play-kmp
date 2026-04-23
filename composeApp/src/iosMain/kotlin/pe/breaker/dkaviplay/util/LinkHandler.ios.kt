package pe.breaker.dkaviplay.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openUrl(url: String?) {
    if (url.isNullOrBlank()) return

    val nsUrl = NSURL.URLWithString(url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}