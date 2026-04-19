package pe.breaker.dkaviplay.util

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

class IOSPermissionManager : PermissionManager {
    override fun requestNotificationPermission() {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or
                    UNAuthorizationOptionBadge or
                    UNAuthorizationOptionSound
        ) { granted, error ->
            if (error != null) {
                println("Error pidiendo permisos: ${error.localizedDescription}")
                return@requestAuthorizationWithOptions
            }

            println("Permiso notificaciones iOS: $granted")
        }
    }
}