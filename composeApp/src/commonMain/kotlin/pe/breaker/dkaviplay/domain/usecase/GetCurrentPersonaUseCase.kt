package pe.breaker.dkaviplay.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.data.entity.PersonaEntity
import pe.breaker.dkaviplay.di.SessionSyncManager

class GetCurrentPersonaUseCase(private val sessionSyncManager: SessionSyncManager) {
    fun execute(): Flow<PersonaEntity?> {
        return sessionSyncManager.personaFlow()
    }
}