package pe.breaker.dkaviplay.core.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.messaging.FirebaseMessaging
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.breaker.dkaviplay.core.data.remote.dto.DataNotificacionDTO
import pe.breaker.dkaviplay.core.data.remote.dto.SendNotificationRequestDTO
import pe.breaker.dkaviplay.core.data.util.ConstatesCloud
import pe.breaker.dkaviplay.core.data.util.handleResponse
import pe.breaker.dkaviplay.core.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging,
    private val getUserUid: () -> String?,
    private val httpClient: HttpClient,
) : NotificationRepository {

    override suspend fun saveToken(token: String?): Result<Unit> {
        val finalToken = if (token.isNullOrEmpty()) firebaseMessaging.getToken() else token
        val userUid = getUserUid() ?: return Result.failure(Exception("No user"))
        return try {
            firestore.collection("UserMovil").document(userUid)
                .update(mapOf("fcmToken" to finalToken))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteToken(): Result<Unit> {
        val userUid = getUserUid() ?: return Result.failure(Exception("No user"))
        return try {
            firestore.collection("UserMovil").document(userUid)
                .update(mapOf("fcmToken" to null))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendNotification(
        user: String, title: String, message: String, action: String
    ): Result<String> {
        val requestBody = SendNotificationRequestDTO(
            uid = user,
            title = title,
            message = message,
            data = DataNotificacionDTO(action = action)
        )
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/notificaciones") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }
}