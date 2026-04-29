package pe.breaker.dkaviplay.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
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

    private fun getOrCreateChannel(): String {
        val channelId = "dkavi_play_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(channelId) == null) {
                val soundUri = "android.resource://$packageName/raw/pool_noti".toUri()
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                nm.createNotificationChannel(
                    NotificationChannel(channelId, "Notificaciones de Pool Street", NotificationManager.IMPORTANCE_HIGH).apply {
                        description = "Canal para reservas y alertas de billar"
                        enableLights(true)
                        lightColor = Color.GREEN
                        setSound(soundUri, audioAttributes)
                    }
                )
            }
        }
        return channelId
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = getOrCreateChannel()
        val soundUri = "android.resource://$packageName/raw/pool_noti".toUri()

        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", remoteMessage.data["action"])
            putExtra("id", remoteMessage.data["id"])
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Pool Street"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_dkavi_play)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}