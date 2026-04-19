package pe.breaker.dkaviplay.domain.repository

interface NotificationRepository {
    suspend fun saveToken(token: String): Result<Unit>
    suspend fun deleteToken() : Result<Unit>
    suspend fun sendNotificacion(user:String, title: String, message: String, accion:String) : Result<String>
}