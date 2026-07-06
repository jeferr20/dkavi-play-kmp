package pe.breaker.dkaviplay.presentation.screen.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import pe.breaker.dkaviplay.domain.model.SessionCheckResult
import pe.breaker.dkaviplay.domain.repository.NotificationRepository
import pe.breaker.dkaviplay.domain.usecase.CheckSessionUseCase
import pe.breaker.dkaviplay.domain.usecase.GetCurrentPersonaUseCase
import pe.breaker.dkaviplay.domain.usecase.LogOutUseCase

class SplashModel(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getCurrentPersonaUseCase: GetCurrentPersonaUseCase,
    private val notificationRepository: NotificationRepository,
    private val logOutUseCase: LogOutUseCase
) : ScreenModel {
    val state = MutableStateFlow<SplashState>(SplashState.Loading)

    fun initialize() {
        screenModelScope.launch {
            state.value = SplashState.Loading
            delay(500)
            try {
                when (val result = checkSessionUseCase()) {
                    is SessionCheckResult.NoSession -> clearAndGoLogin()
                    is SessionCheckResult.NetworkError -> state.value = SplashState.NetworkError
                    is SessionCheckResult.Authenticated -> {
                        launch {
                            try {
                                notificationRepository.saveToken()
                            } catch (e: Exception) {
                                println("Error FCM en Splash: ${e.message}")
                            }
                        }

                        val persona = withTimeoutOrNull(10000) {
                            getCurrentPersonaUseCase.execute().filterNotNull().first()
                        }

                        state.value = when {
                            persona != null -> SplashState.GoToMain
                            else -> SplashState.GoToRegister(result.userId)
                        }
                    }
                }

            } catch (e: Exception) {
                println("❌ Error crítico en SplashScreen: ${e.message}")
                clearAndGoLogin()
            }
        }
    }

    fun retry() {
        initialize()
    }

    private suspend fun clearAndGoLogin() {
        logOutUseCase()
        delay(1000)
        state.value = SplashState.GoToLogin
    }
}