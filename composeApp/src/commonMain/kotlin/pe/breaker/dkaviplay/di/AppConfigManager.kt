package pe.breaker.dkaviplay.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import pe.breaker.dkaviplay.domain.model.AppConfig
import pe.breaker.dkaviplay.domain.usecase.appConfig.ObserveAppConfigUseCase

class AppConfigManager(
    observeAppConfig: ObserveAppConfigUseCase
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val config = observeAppConfig()
        .onEach {
            println("📡 [AppConfigManager] Nueva config: $it")
        }
        .catch { e ->
            println("🔥 [AppConfigManager] Error: ${e.message}")
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly, // 🔥 clave
            initialValue = AppConfig(
                activeServiceMovil = true,
                versionMovil = "1.0.0",
                urlMovilAndroid = "",
                urlMovilIOS = ""
            )
        )
}