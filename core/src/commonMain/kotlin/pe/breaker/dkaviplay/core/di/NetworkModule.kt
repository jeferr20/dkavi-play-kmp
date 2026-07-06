package pe.breaker.dkaviplay.core.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import pe.breaker.dkaviplay.core.data.util.ConstatesCloud
import pe.breaker.dkaviplay.core.data.util.UserSessionManager

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    single { UserSessionManager(get(), get(), get(), get(), get()) }

    single {
        val koin = getKoin()
        val jsonConfig: Json = get()

        HttpClient(provideHttpClientEngine()) {

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
                json(jsonConfig)
            }
        }
    }
}