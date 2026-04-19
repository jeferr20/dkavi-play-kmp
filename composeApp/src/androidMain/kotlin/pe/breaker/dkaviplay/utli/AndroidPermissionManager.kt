package pe.breaker.dkaviplay.utli

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import pe.breaker.dkaviplay.util.PermissionManager

class AndroidPermissionManager(private val context: Context) : PermissionManager {
    override fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val activity = context as? Activity

            if (activity != null) {
                activity.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            } else {
                println("Error: Contexto sigue sin ser Activity.")
            }
        }
    }
}