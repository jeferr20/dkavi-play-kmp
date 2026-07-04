package pe.breaker.dkaviplay.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.core.net.toUri
import kotlinx.coroutines.cancel
import pe.breaker.dkaviplay.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pe.breaker.dkaviplay.R

class FirebaseMessagingService : FirebaseMessagingService(), KoinComponent {
    private val repo: NotificationRepository by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val CHANNEL_ID = "dkavi_play_channel_v1"

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onNewToken(token: String) {
        scope.launch {
            repo.saveToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        println("FCM: Mensaje recibido de ${message.from}")
        println("FCM: Data payload: ${message.notification?.title}")
        println("FCM: Data payload: ${message.notification?.body}")
        if (message.data.isNotEmpty()) {
            showNotification(message)
        }
    }

    private fun getSoundUri(): Uri {
        return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/${R.raw.pool_noti}".toUri()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()

                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de Pool Street",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Canal para reservas y alertas de billar"
                    enableLights(true)
                    lightColor = Color.GREEN
                    setSound(getSoundUri(), audioAttributes)
                }
                nm.createNotificationChannel(channel)
            }
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()

        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            remoteMessage.data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = remoteMessage.data["title"] ?: remoteMessage.notification?.title ?: "DkaviPlay"
        val body = remoteMessage.data["body"] ?: remoteMessage.notification?.body ?: ""

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_dkavi_play)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(getSoundUri())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}