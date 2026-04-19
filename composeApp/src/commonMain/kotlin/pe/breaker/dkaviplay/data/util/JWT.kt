package pe.breaker.dkaviplay.data.util

import io.ktor.util.decodeBase64String
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.domain.model.UserSession
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun decodeJwt(token: String, cripKey: String): UserSession {
    // Un JWT tiene 3 partes: Header.Payload.Signature
    val parts = token.split(".")
    if (parts.size < 2) throw Exception("JWT Inválido")

    // El Payload es la segunda parte (Base64)
    val payload = parts[1].decodeBase64String()
    val json = Json { ignoreUnknownKeys = true }

    // Creamos un modelo temporal para el parseo del JSON interno del JWT
    @Serializable
    data class JwtPayload(val uid: String = "", val role: String = "")
    val data = json.decodeFromString<JwtPayload>(payload)

    return UserSession(
        uid = data.uid,
        role = data.role,
        token = token,
        cripKey = cripKey
    )
}

@OptIn(ExperimentalTime::class)
fun isTokenExpired(token: String): Boolean {
    return try {
        val parts = token.split(".")
        if (parts.size < 2) return true

        // El payload es la segunda parte del JWT
        val payload = parts[1].decodeBase64String()
        val json = Json { ignoreUnknownKeys = true }
        val data = json.decodeFromString<JwtPayload>(payload)

        val currentTime = Clock.System.now().epochSeconds
        data.exp < currentTime // Si el tiempo de expiración es menor al actual, caducó
    } catch (e: Exception) {
        true
    }
}

@Serializable
data class JwtPayload(val exp: Long)