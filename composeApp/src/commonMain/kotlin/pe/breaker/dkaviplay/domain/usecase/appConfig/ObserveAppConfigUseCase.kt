package pe.breaker.dkaviplay.domain.usecase.appConfig

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.domain.model.AppConfig
import pe.breaker.dkaviplay.domain.repository.AppConfigRepository

class ObserveAppConfigUseCase(
    private val repository: AppConfigRepository
) {
    operator fun invoke(): Flow<AppConfig> = repository.observeAppConfig()
}