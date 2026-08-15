package pe.breaker.dkaviplay.di

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.util.SecureStorage

private val refreshMutex = Mutex()

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

    factory {
        provideHttpClient().config {
            install(WebSockets)

            install(Auth) {
                bearer {
                    // 1. Carga el token actual guardado en sesión
                    loadTokens {
                        val sessionManager = get<UserSessionManager>()
                        val token = sessionManager.getToken()
                        if (!token.isNullOrBlank()) {
                            BearerTokens(token, "")
                        } else null
                    }

                    // 2. Se ejecuta automáticamente cuando un endpoint responde 401 Unauthorized
                    refreshTokens {
                        refreshMutex.withLock {
                            val sessionManager = get<UserSessionManager>()
                            val authRepository = get<AuthRepository>()
                            val oldToken = sessionManager.getToken() ?: return@withLock null

                            // Verificamos si otra petición paralela ya renovó el token mientras esperábamos el cerrojo
                            val currentToken = sessionManager.getToken().orEmpty()
                            val requestToken = response.request.headers[HttpHeaders.Authorization]?.replace("Bearer ", "")

                            if (currentToken.isNotEmpty() && currentToken != requestToken) {
                                return@withLock BearerTokens(currentToken, "")
                            }

                            // Ejecutamos la renovación del token
                            val result = authRepository.autoLogin(oldToken)

                            if (result is AutoLoginResult.Success) {
                                val newToken = sessionManager.getToken().orEmpty()
                                BearerTokens(newToken, "")
                            } else {
                                // Si la renovación falla definitivamente, se destruye la sesión local
                                sessionManager.clearSession()
                                null
                            }
                        }
                    }

                    // 3. Define qué peticiones NO deben llevar el Header "Authorization: Bearer <token>"
                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath

                        val publicEndpoints = listOf(
                            "apiPublic/public/login",
                            "apiPublic/public/login/refresh",
                            "apiPublic/public/register/usuario",
                            "apiPublic/public/register/generateCode",
                            "apiPublic/public/register/verifyCode",
                            "apiPublic/public/register/updatePassword"
                        )

                        // Si coincide con una ruta pública retorna false (NO envía el header Authorization)
                        publicEndpoints.none { publicPath -> path.contains(publicPath) }
                    }
                }
            }

            // Headers globales dinámicos para toda la aplicación
            defaultRequest {
                header("x-api-key", ConstatesCloud.APIKEY)
                
                // 💡 Fuerza a que cada petición consulte el token actual en RAM
                val sessionManager = get<UserSessionManager>()
                val token = sessionManager.getToken()
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
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