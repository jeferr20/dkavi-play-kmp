package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.remote.supabase.MesaDTO
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Mesa
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.MesaRepository

class MesaRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager
) : MesaRepository {
    override suspend fun getMesasBySede(sedeUid: String): Result<List<Mesa>> {
        return try {
            val sedeIdInt = sedeUid.toIntOrNull() ?: return Result.success(emptyList())

            val mesasList = safeSupabaseCall {
                supabaseClient
                    .from(schema = "public", table = "Mesa")
                    .select {
                        filter {
                            eq("status", true)
                            eq("id_sede", sedeIdInt)
                        }
                        order("descripcion", Order.ASCENDING)
                    }.decodeList<MesaDTO>()
            }

            val mesas = mesasList.map { mesa ->
                Mesa(
                    mesaUid = mesa.id.toString(),
                    nombreMesa = mesa.descripcion
                )
            }
            Result.success(mesas)
        } catch (e: Exception) {
            println("Error GET MESAS: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun <T> safeSupabaseCall(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            if (errorMsg.contains("JWT expired", ignoreCase = true) || errorMsg.contains("PGRST303")) {
                println("🔄 [MesaRepository] Supabase JWT expirado detectado. Intentando autoLogin...")
                val token = sessionManager.getToken()
                if (!token.isNullOrBlank()) {
                    val result = authRepository.autoLogin(token)
                    if (result is AutoLoginResult.Success) {
                        println("✅ [MesaRepository] Sesión refrescada con éxito. Reintentando operación...")
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