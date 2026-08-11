package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.messaging.FirebaseMessaging
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pe.breaker.dkaviplay.data.remote.dto.DataNotificacionDTO
import pe.breaker.dkaviplay.data.remote.dto.SendNotificationRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.request.RequestUpdateTokenDTO
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val firebaseMessaging: FirebaseMessaging,
    private val httpClient: HttpClient,
) : NotificationRepository {

    override suspend fun saveToken(token: String?): Result<Unit> {
        val finalToken = if (token.isNullOrEmpty()) firebaseMessaging.getToken() else token

        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/notificaciones/updateFCMToken") {
                contentType(ContentType.Application.Json)
                setBody(RequestUpdateTokenDTO(finalToken))
            }
        return handleResponse<String, Unit>(response) { msg ->
            println("Respuesta del server: $msg")
            Result.success(Unit)
        }
    }

    override suspend fun deleteToken(): Result<Unit> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/notificaciones/updateFCMToken") {
                contentType(ContentType.Application.Json)
                setBody(RequestUpdateTokenDTO(null))
            }
        return handleResponse<String, Unit>(response) { msg ->
            println("Respuesta del server: $msg")
            Result.success(Unit)
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
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/notificaciones/send") {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }
}