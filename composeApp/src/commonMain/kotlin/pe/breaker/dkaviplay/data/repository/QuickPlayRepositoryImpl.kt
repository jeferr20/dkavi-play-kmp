package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import pe.breaker.dkaviplay.data.mapper.toDomain
import pe.breaker.dkaviplay.data.remote.supabase.rpc.UserQuickDTO
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository

class QuickPlayRepositoryImpl(
    private val sessionManager: UserSessionManager,
    private val supabaseClient: SupabaseClient,
) : QuickPlayRepository {

    override suspend fun searchUsers(userToSearch: String): Result<List<UserQuick>> {
        return try {
            val currentUserUid = sessionManager.getUserUid()
            val currentDepartamento = sessionManager.getCurrentUsuario()?.departamento
            val currentProvincia = sessionManager.getCurrentUsuario()?.provincia

            val userListDto = supabaseClient.postgrest.rpc(
                function = "buscar_usuarios_quickplay",
                parameters = mapOf(
                    "p_departamento" to currentDepartamento,
                    "p_provincia" to currentProvincia,
                    "p_busqueda" to userToSearch.trim()
                )
            ) {
                schema = "seguridad"
            }.decodeList<UserQuickDTO>()

            val users = userListDto
//                .filter { it.uuidAuth != currentUserUid }
                .map { it.toDomain() }

            Result.success(users)
        } catch (e: Exception) {
            println("Error buscando usuarios QuickPlay: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun getUser(userUid: String): Result<UserQuick> {
        return try {
            val userDTO = supabaseClient.postgrest.rpc(
                function = "get_usuario_quickplay",
                parameters = mapOf(
                    "p_user_uid" to userUid
                )
            ) {
                schema = "seguridad"
            }.decodeSingleOrNull<UserQuickDTO>()

            if (userDTO == null) {
                return Result.failure(Exception("El usuario no existe en la base de datos."))
            }
            Result.success(userDTO.toDomain())
        } catch (e: Exception) {
            println("Error buscando usuarios QuickPlay: ${e.message}")
            Result.failure(e)
        }
    }
}