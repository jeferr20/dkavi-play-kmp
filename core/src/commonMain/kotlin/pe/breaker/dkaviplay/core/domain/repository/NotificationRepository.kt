package pe.breaker.dkaviplay.core.domain.repository

interface NotificationRepository {
    suspend fun saveToken(token:String? = null): Result<Unit>
    suspend fun deleteToken() : Result<Unit>
    suspend fun sendNotification(user: String, title: String, message: String, action: String): Result<String>
}