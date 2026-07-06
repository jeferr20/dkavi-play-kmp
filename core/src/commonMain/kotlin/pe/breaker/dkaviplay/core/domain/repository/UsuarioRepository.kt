package pe.breaker.dkaviplay.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.core.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio

interface UsuarioRepository {
    fun getUsuarioStream(usuarioUid: String): Flow<UsuarioEntity?>
    suspend fun otorgarRecompensas(userId: String, recompensas: List<DetallePremio>) : Result<String>
    suspend fun usarRecompensas(userId: String, itemId: Int,reservaId: String, isCreador : Boolean) : Result<String>
}