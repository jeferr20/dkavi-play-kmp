package pe.breaker.dkaviplay.data.repository

import com.russhwolf.settings.Settings
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
import pe.breaker.dkaviplay.data.provider.FirebaseAuthProvider
import pe.breaker.dkaviplay.data.provider.FirestoreProvider
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.LoginResponseDto
import pe.breaker.dkaviplay.data.remote.RegisterUsuarioRequestDto
import pe.breaker.dkaviplay.data.remote.dto.LoginRequestDto
import pe.breaker.dkaviplay.data.remote.dto.SendCodeRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.TimeResponse
import pe.breaker.dkaviplay.data.remote.dto.UpdatePasswordRequestDTO
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.decodeJwt
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.domain.model.UserSession
import pe.breaker.dkaviplay.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val settings: Settings,
    private val firestoreProvider: FirestoreProvider,
    private val firebaseAuthProvider: FirebaseAuthProvider,
//    private val context: Any?
) : AuthRepository{

    override suspend fun login(usuario: String, pass: String): Result<UserSession> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(usuario, pass))
        }

        return handleResponse<LoginResponseDto, UserSession>(response) { data ->
            firebaseAuthProvider.signInWithCustomToken(data.firebaseToken)
            val session = decodeJwt(data.token, data.cripKey)
            settings.putString("auth_token", session.token)
            settings.putString("current_user_uid", session.uid)
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

            val result = handleResponse<LoginResponseDto, UserSession>(response) { data ->
                val session = decodeJwt(data.token, data.cripKey)
                settings.putString("auth_token", session.token)
                settings.putString("current_user_uid", session.uid)
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
            setBody(LoginRequestDto(usuario, pass))
        }

        return handleResponse<String, String>(response) { uid ->
            settings.putString("current_user_uid", uid)
            Result.success(uid)
        }
    }

    override suspend fun registerPersona(persona: RegisterUsuarioRequestDto): Result<String> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/register/persona") {
            contentType(ContentType.Application.Json)
            setBody(persona)
        }

        return handleResponse<String, String>(response) { data ->
            settings.remove("current_user_uid")
            Result.success(data)
        }
    }

    override suspend fun uploadProfileImage(byteArray: ByteArray): Result<String> {
        return try {
            /*
            val uid = settings.getStringOrNull("current_user_uid")
                ?: return Result.failure(Exception("No User UID"))

            val storageRef = Firebase.storage.reference.child("Perfiles/$uid.webp")
            val fileToUpload = byteArray.toFirebaseFile(context)

            storageRef.putFile(fileToUpload)

            val url = storageRef.getDownloadUrl()

            val userUid = settings.getStringOrNull("current_user_uid")
            if (userUid != null) {
                firestore.collection("UserMovil")
                    .document(userUid)
                    .update(mapOf("urlImagen" to url))

                Result.success(url)

            } else {
                Result.failure(Exception("No se encontró el usuario en Firestore"))
            }*/
            Result.success("")
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
//            val tiempoActualMs = try {
//                val response: TimeResponse = httpClient.get(ConstatesCloud.TIMEAPI).body()
//                response.unixtime * 1000
//            } catch (e: Exception) {
//                return Result.failure(Exception("Error al validar la hora del servidor: ${e.message}"))
//            }
//
//            val personaFirebase = firestoreProvider.getPersona(userId)
//                ?: return Result.failure(Exception("Usuario no encontrado"))
//
//            val codigoFirestore = personaFirebase.codigoRecuperacion
//            val expiracionMilis = personaFirebase.codigoExpiracion
//
//            if(codigoFirestore.isNullOrEmpty() || expiracionMilis == null){
//                return Result.failure(Exception("No hay un proceso de recuperación activo"))
//            }
//
//            val result = when {
//                codigoFirestore != code -> false
//                tiempoActualMs > expiracionMilis -> false
//                else -> true
//            }
            Result.success(true)
        }catch (e: Exception) {
            Result.failure(e)
        }
    }
}