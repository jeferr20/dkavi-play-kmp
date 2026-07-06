package pe.breaker.dkaviplay.core.domain.repository

import dev.gitlive.firebase.auth.FirebaseUser
import pe.breaker.dkaviplay.core.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.core.data.remote.dto.RegisterUsuarioRequestDto
import pe.breaker.dkaviplay.core.domain.model.UserSession

interface AuthRepository {
    suspend fun login(usuario: String, pass: String): Result<UserSession>
    suspend fun autoLogin(token: String): AutoLoginResult
    suspend fun registerUser(usuario: String, pass: String): Result<String>
    suspend fun registerPersona(persona: RegisterUsuarioRequestDto, isLogged: Boolean): Result<String>
    suspend fun uploadProfileImage(byteArray: ByteArray): Result<String>
    suspend fun sendCode(email: String, celular: String): Result<String>
    suspend fun actualizarPassword(userId:String,newPassword: String): Result<String>
    suspend fun verifyPassword(userId:String,code:String) : Result<Boolean>
    suspend fun isUserLoggedIn() : Boolean
    suspend fun logout(): Result<Unit>
}