package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.koin.mp.KoinPlatform.getKoin
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.dto.LoginRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.LoginResponseDTO
import pe.breaker.dkaviplay.data.remote.dto.SendCodeRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.UpdatePasswordRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.request.RequestRegisterInfoUsuarioDTO
import pe.breaker.dkaviplay.data.remote.dto.request.RequestVerifyCodeDTO
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.decodeJwt
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.di.SessionSyncManager
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.LoginResult
import pe.breaker.dkaviplay.domain.model.UserSession
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.util.toFirebaseData
import kotlin.time.Clock

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionManager: UserSessionManager,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val fireStorage: FirebaseStorage,
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    override suspend fun login(usuario: String, pass: String): Result<LoginResult> {
        sessionManager.clearSession()

        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDTO(usuario, pass))
        }

        return handleResponse<LoginResponseDTO, LoginResult>(response) { data ->
            val rawJwtToken = data.token

            if (rawJwtToken.isNullOrEmpty() || data.cripKey.isNullOrEmpty()) {
                return Result.failure(Exception("Token o clave de cifrado nula"))
            }

            val sessionDecoded = decodeJwt(rawJwtToken, data.cripKey)

            // CASO A: Usuario Incompleto -> Guardar JWT temporal y redirigir
            if (data.usuarioCompleto == false) {
                sessionManager.saveSession(
                    token = rawJwtToken,
                    uid = sessionDecoded.uid,
                    userId = sessionDecoded.userId,
                    firebaseToken = null,
                    supabaseToken = null,
                    supabaseRefreshToken = null
                )
                sessionManager.loadSession()
                return@handleResponse Result.success(LoginResult.Incomplete(sessionDecoded.userId))
            }

            // CASO B: Usuario Completo -> Importar SDKs y guardar sesión completa
            data.supabaseToken?.let { accessToken ->
                try {
                    val currentRefreshToken = data.refreshToken ?: ""
                    supabaseClient.auth.importSession(
                        io.github.jan.supabase.auth.user.UserSession(
                            accessToken = accessToken,
                            refreshToken = currentRefreshToken,
                            expiresIn = 3600,
                            tokenType = "bearer",
                            user = null
                        )
                    )
                } catch (e: Exception) {
                    println("Error importando la sesión en Supabase móvil: ${e.message}")
                }
            }

            data.firebaseToken?.let { firebaseAuth.signInWithCustomToken(it) }

            sessionManager.saveSession(
                token = rawJwtToken,
                uid = sessionDecoded.uid,
                userId = sessionDecoded.userId,
                firebaseToken = data.firebaseToken,
                supabaseToken = data.supabaseToken,
                supabaseRefreshToken = data.refreshToken
            )

            Result.success(LoginResult.Success(sessionDecoded))
        }
    }

    override suspend fun autoLogin(token: String): AutoLoginResult {
        return try {
            val currentSupabaseRefreshToken = sessionManager.getSupabaseRefreshToken() ?: ""

            val response =
                httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/login/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(mapOf("refreshTokenMovil" to currentSupabaseRefreshToken))
                }

            if (response.status == HttpStatusCode.Unauthorized) {
                return AutoLoginResult.InvalidToken
            }

            val result = handleResponse<LoginResponseDTO, UserSession>(response) { data ->
                val rawJwtToken = data.token

                if (rawJwtToken.isNullOrEmpty() || data.cripKey.isNullOrEmpty()) {
                    return AutoLoginResult.InvalidToken
                }

                val session = decodeJwt(rawJwtToken, data.cripKey)

                // Re-importar sesión de Supabase si llegó un nuevo token
                data.supabaseToken?.let { accessToken ->
                    try {
                        supabaseClient.auth.importSession(
                            io.github.jan.supabase.auth.user.UserSession(
                                accessToken = accessToken,
                                refreshToken = data.refreshToken ?: "",
                                expiresIn = 3600,
                                tokenType = "bearer",
                                user = null
                            )
                        )
                    } catch (e: Exception) {
                        println("Error actualizando la sesión en Supabase móvil: ${e.message}")
                    }
                }

                // Re-autenticar Firebase si llegó un nuevo token
                data.firebaseToken?.let { firebaseAuth.signInWithCustomToken(it) }

                // 🔒 Guardar tanto el nuevo JWT como el NUEVO refreshToken de Supabase rotado
                sessionManager.saveSession(
                    token = rawJwtToken, // 💡 JWT String original
                    uid = session.uid,
                    userId = session.userId,
                    firebaseToken = data.firebaseToken,
                    supabaseToken = data.supabaseToken,
                    supabaseRefreshToken = data.refreshToken // 💡 Guarda la rotación del refresh_token
                )
                sessionManager.loadSession() // Actualiza los campos en la memoria RAM

                Result.success(session)
            }

            if (result.isSuccess) {
                AutoLoginResult.Success(result.getOrThrow())
            } else {
                AutoLoginResult.InvalidToken
            }

        } catch (e: Exception) {
            AutoLoginResult.NetworkError(e.message ?: "Error de red")
        }
    }

    override suspend fun registerUser(usuario: String, pass: String): Result<Int> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/usuario") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequestDTO(usuario, pass))
            }

        return handleResponse<LoginResponseDTO, Int>(response) { data ->
            if (data.token.isNullOrEmpty() || data.cripKey.isNullOrEmpty()) {
                return Result.failure(Exception("Token o clave de cifrado nula"))
            }
            val sessionTemporal = decodeJwt(data.token, data.cripKey)
            sessionManager.saveSession(
                token = sessionTemporal.token,
                uid = sessionTemporal.uid,
                userId = sessionTemporal.userId,
                firebaseToken = null,
                supabaseToken = null,
                supabaseRefreshToken = null
            )
            sessionManager.loadSession() // Aseguramos que los datos estén en memoria antes de retornar
            Result.success(sessionTemporal.userId)
        }
    }

    override suspend fun registerPersona(
        persona: RequestRegisterInfoUsuarioDTO, isLogged: Boolean
    ): Result<String> {
        val token = sessionManager.getToken()
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/persona") {
                if (!token.isNullOrBlank()) {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(persona)
            }

        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun uploadProfileImage(byteArray: ByteArray): Result<String> {
        return runCatching {
            val userUidAuth = sessionManager.getUserUid() // uuid_auth (String)
                ?: return Result.failure(Exception("No se encontró el UID de autenticación"))

            val storageRef = fireStorage.reference.child("Perfiles/$userUidAuth.webp")
            val data = byteArray.toFirebaseData()
            storageRef.putData(data)

            val urlPublica = storageRef.getDownloadUrl()

            supabaseClient
                .from(schema = "seguridad", table = "UserMovil")
                .update(
                    mapOf(
                        "urlImagen" to urlPublica,
                        "date_up" to Clock.System.now().toString()
                    )
                ) {
                    filter { eq("uuid_auth", userUidAuth) }
                }

            runCatching {
                firestore.collection("UserMovil")
                    .document(userUidAuth)
                    .update(mapOf("urlImagen" to urlPublica))
            }.onFailure { e ->
                println("⚠️ Advertencia: No se pudo replicar la URL en Firestore: ${e.message}")
            }

            urlPublica
        }
    }

    override suspend fun sendCode(email: String, celular: String): Result<Int> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/generateCode") {
                contentType(ContentType.Application.Json)
                setBody(SendCodeRequestDTO(email, celular))
            }

        return handleResponse<Int, Int>(response) { userUid ->
            Result.success(userUid)
        }
    }

    override suspend fun actualizarPassword(userId: Int, newPassword: String): Result<String> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/updatePassword") {
                contentType(ContentType.Application.Json)
                setBody(UpdatePasswordRequestDTO(userId, newPassword))
            }

        return handleResponse<String, String>(response) { message ->
            Result.success(message)
        }
    }

    override suspend fun verifyPassword(userId: Int, code: String): Result<String> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/verifyCode") {
                contentType(ContentType.Application.Json)
                setBody(RequestVerifyCodeDTO(userId, code))
            }
        return handleResponse<String, String>(response) { message ->
            Result.success(message)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            // Aseguramos que el sessionManager tenga los datos cargados en memoria
            sessionManager.loadSession()

            val localToken = sessionManager.getToken()
            val userUid = sessionManager.getUserUid()
            val internalId = sessionManager.getUserId()

            !localToken.isNullOrBlank() && !userUid.isNullOrBlank() && internalId != null
        } catch (e: Exception) {
            println("Error verificando consistencia de estados de sesión: ${e.message}")
            false
        }
    }

    override suspend fun logOut() {
        // 1. Detener la sincronización reactiva inmediatamente para evitar que intente escribir en DB mientras limpiamos
        val syncManager: SessionSyncManager = getKoin().get()
        syncManager.stopSync()

        // 2. Limpiar base de datos local y almacenamiento seguro (Con manejo de red seguro dentro)
        sessionManager.clearSession()

        // 3. Cerrar sesión en Firebase de forma segura
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            println("Error al cerrar sesión en Firebase: ${e.message}")
        }

        // 4. Cerrar sesión en Supabase de forma segura (Previene caídas por falta de red)
        try {
            supabaseClient.auth.clearSession()
        } catch (e: Exception) {
            println("Error al cerrar sesión en Supabase Auth: ${e.message}")
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        val uidAuth = sessionManager.getUserUid()
//        val token = sessionManager.getToken()
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/usuario/delete") {
//            if (!token.isNullOrBlank()) {
//                header(HttpHeaders.Authorization, "Bearer $token")
//            }
            contentType(ContentType.Application.Json)
            setBody(mapOf("userUid" to uidAuth))
        }
        return handleResponse<String, Unit>(response) {
            logOut()
            Result.success(Unit)
        }
    }
}