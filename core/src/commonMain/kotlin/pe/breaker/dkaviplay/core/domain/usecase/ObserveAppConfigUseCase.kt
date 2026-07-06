package pe.breaker.dkaviplay.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.core.domain.model.AppConfig
import pe.breaker.dkaviplay.core.domain.repository.AppConfigRepository

class ObserveAppConfigUseCase(
    private val repository: AppConfigRepository
) {
    operator fun invoke(): Flow<AppConfig> = repository.observeAppConfig()
}