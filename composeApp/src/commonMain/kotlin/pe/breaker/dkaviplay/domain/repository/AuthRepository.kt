package pe.breaker.dkaviplay.domain.repository

import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.dto.request.RequestRegisterInfoUsuarioDTO
import pe.breaker.dkaviplay.domain.model.LoginResult

interface AuthRepository {
    suspend fun login(usuario: String, pass: String): Result<LoginResult>
    suspend fun autoLogin(token: String): AutoLoginResult
    suspend fun registerUser(usuario: String, pass: String): Result<Int>
    suspend fun registerPersona(persona: RequestRegisterInfoUsuarioDTO, isLogged: Boolean): Result<String>
    suspend fun uploadProfileImage(byteArray: ByteArray): Result<String>
    suspend fun sendCode(email: String, celular: String): Result<Int>
    suspend fun actualizarPassword(userId:Int,newPassword: String): Result<String>
    suspend fun verifyPassword(userId:Int,code:String) : Result<String>
    suspend fun isLoggedIn() : Boolean
    suspend fun logOut()
    suspend fun deleteAccount(): Result<Unit>
}