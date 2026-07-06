package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import pe.breaker.dkaviplay.data.remote.dto.RegisterUsuarioRequestDto
import pe.breaker.dkaviplay.data.remote.dto.SendCodeRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.TimeResponse
import pe.breaker.dkaviplay.data.remote.dto.UpdatePasswordRequestDTO
import pe.breaker.dkaviplay.data.remote.firebase.PersonaFirebase
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.decodeJwt
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.di.SessionSyncManager
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.UserSession
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.util.toFirebaseData

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionManager: UserSessionManager,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val fireStorage: FirebaseStorage
    ) : AuthRepository {
    override suspend fun login(
        usuario: String,
        pass: String
    ): Result<UserSession> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDTO(usuario, pass))
        }

        return handleResponse<LoginResponseDTO, UserSession>(response) { data ->
            data.firebaseToken?.let { firebaseAuth.signInWithCustomToken(it) }
            val session = decodeJwt(data.token, data.cripKey)
            sessionManager.saveSession(session.token, session.uid)
            Result.success(session)
        }
    }

    override suspend fun autoLogin(token: String): AutoLoginResult {
        return try {
            val response = httpClient.get("${ConstatesCloud.URLBASE}apiPublic/public/login/refresh") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }

            if (response.status == HttpStatusCode.Unauthorized) {
                return AutoLoginResult.InvalidToken
            }

            val result = handleResponse<LoginResponseDTO, UserSession>(response) { data ->
                val session = decodeJwt(data.token, data.cripKey)
                sessionManager.saveSession(session.token, session.uid)
                Result.success(session)
            }

            if (result.isSuccess) AutoLoginResult.Success else AutoLoginResult.InvalidToken

        } catch (e: Exception) {
            AutoLoginResult.NetworkError(e.message ?: "Error de red")
        }
    }

    override suspend fun registerUser(usuario: String, pass: String): Result<String> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/usuario") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDTO(usuario, pass))
        }

        return handleResponse<String, String>(response) { uid ->
            sessionManager.saveSession(null,uid)
            Result.success(uid)
        }
    }

    override suspend fun registerPersona(persona: RegisterUsuarioRequestDto, isLogged: Boolean): Result<String> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/persona") {
            contentType(ContentType.Application.Json)
            setBody(persona)
        }

        return handleResponse<String, String>(response) { data ->
            if(!isLogged) sessionManager.clearSession()
            Result.success(data)
        }
    }

    override suspend fun uploadProfileImage(byteArray: ByteArray): Result<String> {
        return try {
            val uid = sessionManager.getUserUid()
                ?: return Result.failure(Exception("No User UID"))

            val storageRef = fireStorage.reference.child("Perfiles/$uid.webp")
            val data = byteArray.toFirebaseData()
            storageRef.putData(data)

            val url = storageRef.getDownloadUrl()

            firestore.collection("UserMovil")
                .document(uid)
                .update(mapOf("urlImagen" to url))

            Result.success(url)
        } catch (e: Exception) {
            println("Error en uploadProfileImage: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendCode(
        email: String,
        celular: String
    ): Result<String> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/generateCode") {
            contentType(ContentType.Application.Json)
            setBody(SendCodeRequestDTO(email, celular))
        }

        return handleResponse<String, String>(response) { userUid ->
            Result.success(userUid)
        }
    }

    override suspend fun actualizarPassword(userId:String,newPassword: String): Result<String> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/updatePassword") {
            contentType(ContentType.Application.Json)
            setBody(UpdatePasswordRequestDTO(userId, newPassword))
        }

        return handleResponse<String, String>(response) { message ->
            Result.success(message)
        }
    }

    override suspend fun verifyPassword(userId:String,code: String): Result<Boolean> {
        return  try{
            val tiempoActualMs = try {
                val response: TimeResponse = httpClient.get(ConstatesCloud.TIMEAPI).body()
                response.unixtime * 1000
            } catch (e: Exception) {
                return Result.failure(Exception("Error al validar la hora del servidor: ${e.message}"))
            }

            val snapshot = firestore.collection("Persona").document(userId).get()

            if (!snapshot.exists) {
                return Result.failure(Exception("Usuario no encontrado"))
            }

            val personaFirebase = snapshot.data<PersonaFirebase>()
            val codigoFirestore = personaFirebase.codigoRecuperacion
            val expiracionMilis = personaFirebase.codigoExpiracion

            if(codigoFirestore.isNullOrEmpty() || expiracionMilis == null){
                return Result.failure(Exception("No hay un proceso de recuperación activo"))
            }

            val result = when {
                codigoFirestore != code -> false
                tiempoActualMs > expiracionMilis -> false
                else -> true
            }
            Result.success(result)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun logOut() {
        val syncManager: SessionSyncManager = getKoin().get()
        syncManager.stopSync()
        sessionManager.clearSession()
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try{
            logOut()
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ Error crítico al eliminar la cuenta: ${e.message}")
            Result.failure(e)
        }
    }
}