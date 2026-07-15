package pe.breaker.dkaviplay.di

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.util.SecureStorage

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
            prettyPrint = true
        }
    }

    single {
        SecureStorage(
            vault = get(named("vault"))
        )
    }

    single {
        val driverFactory: DatabaseDriverFactory = get()
        AppDatabase(driverFactory.createDriver())
    }

    single { Database(get<DatabaseDriverFactory>()) }

    single {
        provideHttpClient().config {
            install(WebSockets)
            defaultRequest {
                val sessionManager = get<UserSessionManager>()
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
                json(get<Json>())
            }
        }
    }
}