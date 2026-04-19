package pe.breaker.dkaviplay.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import pe.breaker.dkaviplay.data.util.ConstatesCloud

val networkModule = module {
    single {
        val koin = getKoin()
        HttpClient {
            defaultRequest {
                val sessionManager = koin.get<UserSessionManager>()
                val token = sessionManager.getToken()
                val path = url.encodedPath

                val isPublicRoute = path.contains("/login") || path.contains("/registro")

                if (!isPublicRoute && !token.isNullOrBlank()) {
                    header("Authorization", "Bearer $token")
                }

                header("x-api-key", ConstatesCloud.APIKEY)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000L
                connectTimeoutMillis = 15_000L
                socketTimeoutMillis = 30_000L
            }
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
    }
}