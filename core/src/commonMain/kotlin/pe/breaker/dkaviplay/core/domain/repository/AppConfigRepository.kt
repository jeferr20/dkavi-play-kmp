package pe.breaker.dkaviplay.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.core.domain.model.AppConfig

interface AppConfigRepository {
    fun observeAppConfig(): Flow<AppConfig>
}