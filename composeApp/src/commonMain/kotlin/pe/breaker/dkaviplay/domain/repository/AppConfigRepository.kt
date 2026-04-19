package pe.breaker.dkaviplay.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.domain.model.AppConfig

interface AppConfigRepository {
    fun observeAppConfig(): Flow<AppConfig>
}