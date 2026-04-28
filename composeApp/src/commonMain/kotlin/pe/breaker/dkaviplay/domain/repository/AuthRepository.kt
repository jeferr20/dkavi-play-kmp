package pe.breaker.dkaviplay.domain.repository

import dev.gitlive.firebase.auth.FirebaseUser
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.dto.RegisterUsuarioRequestDto
import pe.breaker.dkaviplay.domain.model.UserSession

interface AuthRepository {
    suspend fun login(usuario: String, pass: String): Result<UserSession>
    suspend fun autoLogin(token: String): AutoLoginResult
    suspend fun registerUser(usuario: String, pass: String): Result<String>
    suspend fun registerPersona(persona: RegisterUsuarioRequestDto, isLogged: Boolean): Result<String>
    suspend fun uploadProfileImage(byteArray: ByteArray): Result<String>
    suspend fun sendCode(email: String, celular: String): Result<String>
    suspend fun actualizarPassword(userId:String,newPassword: String): Result<String>
    suspend fun verifyPassword(userId:String,code:String) : Result<Boolean>
    suspend fun getFirebaseUser() : FirebaseUser?
}