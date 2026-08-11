package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import pe.breaker.dkaviplay.data.mapper.toDomain
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.supabase.rpc.UserQuickDTO
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository

class QuickPlayRepositoryImpl(
    private val sessionManager: UserSessionManager,
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository
) : QuickPlayRepository {

    override suspend fun searchUsers(userToSearch: String): Result<List<UserQuick>> {
        return try {
            val currentUserUid = sessionManager.getUserUid() ?: ""
            val currentUsuario = sessionManager.getCurrentUsuario()

            val userListDto = safeSupabaseCall {
                supabaseClient.postgrest.rpc(
                    function = "buscar_usuarios_quickplay",
                    parameters = mapOf(
                        "p_departamento" to (currentUsuario?.departamento ?: ""),
                        "p_provincia" to (currentUsuario?.provincia ?: ""),
                        "p_busqueda" to userToSearch.lowercase().trim(),
                        "p_user_uid" to currentUserUid
                    )
                ) {
                    schema = "seguridad"
                }.decodeList<UserQuickDTO>()
            }

            val users = userListDto.map { it.toDomain() }

            Result.success(users)
        } catch (e: Exception) {
            println("Error buscando usuarios QuickPlay: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUser(userUid: String): Result<UserQuick> {
        return try {
            val userDTO = safeSupabaseCall {
                supabaseClient.postgrest.rpc(
                    function = "get_usuario_quickplay",
                    parameters = mapOf(
                        "p_user_uid" to userUid
                    )
                ) {
                    schema = "seguridad"
                }.decodeSingleOrNull<UserQuickDTO>()
            }

            if (userDTO == null) {
                return Result.failure(Exception("El usuario no existe en la base de datos."))
            }
            Result.success(userDTO.toDomain())
        } catch (e: Exception) {
            println("Error buscando usuarios QuickPlay: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun <T> safeSupabaseCall(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("JWT expired", ignoreCase = true) || errorMsg.contains("PGRST303")) {
                println("🔄 [QuickPlayRepository] Supabase JWT expirado detectado. Intentando autoLogin...")
                val token = sessionManager.getToken()
                if (!token.isNullOrBlank()) {
                    val result = authRepository.autoLogin(token)
                    if (result is AutoLoginResult.Success) {
                        println("✅ [QuickPlayRepository] Sesión refrescada con éxito. Reintentando operación...")
                        return block() // Reintento único
                    }
                }
                throw e
            } else {
                throw e
            }
        }
    }
}