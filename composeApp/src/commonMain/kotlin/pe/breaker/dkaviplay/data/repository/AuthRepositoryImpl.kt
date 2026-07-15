package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.ktor.client.HttpClient
import io.ktor.client.request.get
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
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDTO(usuario, pass))
        }

        return handleResponse<LoginResponseDTO, LoginResult>(response) { data ->
            if (data.usuarioCompleto == false && data.usuarioId != null) {
                return Result.success(LoginResult.Incomplete(data.usuarioId))
            }
            data.supabaseToken?.let { token ->
                try {
                    supabaseClient.auth.importSession(
                        io.github.jan.supabase.auth.user.UserSession(
                            accessToken = token,
                            refreshToken = "", // El backend maneja el refresco o se vuelve a loguear si expira por completo
                            expiresIn = 3600,  // Tiempo estimado por defecto de Supabase (1 hora)
                            tokenType = "bearer",
                            user = null
                        )
                    )
                } catch (e: Exception) {
                    println("Error importando la sesión en Supabase móvil: ${e.message}")
                }
            }
            data.firebaseToken?.let { firebaseAuth.signInWithCustomToken(it) }
            if (data.token.isNullOrEmpty() || data.cripKey.isNullOrEmpty()) {
                return Result.failure(Exception("Token o clave de cifrado nula"))
            }
            val session = decodeJwt(data.token, data.cripKey)
            sessionManager.saveSession(
                token = session.token,
                uid = session.uid,
                userId = session.userId,
                firebaseToken = data.firebaseToken,
                supabaseToken = data.supabaseToken
            )
            Result.success(LoginResult.Success(session))
        }
    }

    override suspend fun autoLogin(token: String): AutoLoginResult {
        return try {
            val response =
                httpClient.get("${ConstatesCloud.URLBASE}apiPublic/public/login/refresh") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }

            if (response.status == HttpStatusCode.Unauthorized) {
                return AutoLoginResult.InvalidToken
            }

            val result = handleResponse<LoginResponseDTO, UserSession>(response) { data ->
                if (data.token.isNullOrEmpty() || data.cripKey.isNullOrEmpty()) {
                    return AutoLoginResult.InvalidToken
                }
                val session = decodeJwt(data.token, data.cripKey)
                sessionManager.saveSession(
                    token = session.token,
                    uid = session.uid,
                    userId = session.userId,
                    firebaseToken = data.firebaseToken,
                    supabaseToken = data.supabaseToken
                )
                Result.success(session)
            }

            if (result.isSuccess) AutoLoginResult.Success else AutoLoginResult.InvalidToken

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

        return handleResponse<Int, Int>(response) { uid ->
            Result.success(uid)
        }
    }

    override suspend fun registerPersona(
        persona: RequestRegisterInfoUsuarioDTO, isLogged: Boolean
    ): Result<String> {
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/persona") {
                contentType(ContentType.Application.Json)
                setBody(persona)
            }

        return handleResponse<String, String>(response) { data ->
            if (!isLogged) sessionManager.clearSession()
            Result.success(data)
        }
    }

    override suspend fun uploadProfileImage(byteArray: ByteArray): Result<String> {
        return try {
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

            try {
                firestore.collection("UserMovil")
                    .document(userUidAuth)
                    .update(mapOf("urlImagen" to urlPublica))
            } catch (e: Exception) {
                println("⚠️ Advertencia: No se pudo replicar la URL en Firestore: ${e.message}")
            }
            Result.success(urlPublica)
        } catch (e: Exception) {
            println("Error en uploadProfileImage: ${e.message}")
            Result.failure(e)
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
            val hasFirebaseSession = firebaseAuth.currentUser != null
            val hasSupabaseSession = supabaseClient.auth.currentSessionOrNull() != null
            val hasLocalApiToken = !sessionManager.getToken().isNullOrBlank()

            hasFirebaseSession && hasSupabaseSession && hasLocalApiToken
        } catch (e: Exception) {
            println("Error verificando consistencia de estados de sesión: ${e.message}")
            false
        }
    }

    override suspend fun logOut() {
        val syncManager: SessionSyncManager = getKoin().get()
        syncManager.stopSync()
        sessionManager.clearSession()
        firebaseAuth.signOut()
        supabaseClient.auth.clearSession()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        val uidAuth = sessionManager.getUserUid()
        val response =
            httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/verifyCode") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("user" to uidAuth))
            }
        return handleResponse<String, Unit>(response) {
            logOut()
            Result.success(Unit)
        }
    }
}