package pe.breaker.dkaviplay.presentation.screen.splash

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import pe.breaker.dkaviplay.core.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.core.data.util.UserSessionManager
import pe.breaker.dkaviplay.core.data.util.isTokenExpired
import pe.breaker.dkaviplay.core.domain.repository.AuthRepository
import pe.breaker.dkaviplay.core.domain.repository.NotificationRepository

class SplashModel(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val sessionManager: UserSessionManager
) : ScreenModel {
    val state = MutableStateFlow<SplashState>(SplashState.Loading)

    fun initialize() {
        screenModelScope.launch {
            sessionManager.loadSession()
            delay(500)
            try {
                val token = sessionManager.getToken()
                val userId = sessionManager.getUserUid()

                if (token == null || userId == null) {
                    clearAndGoLogin()
                    return@launch
                }

                if (isTokenExpired(token)) {
                    val isLogged = authRepository.isUserLoggedIn()
                    if (!isLogged) {
                        clearAndGoLogin()
                        return@launch
                    }

                    when (authRepository.autoLogin(token)) {
                        is AutoLoginResult.InvalidToken -> {
                            clearAndGoLogin()
                            return@launch
                        }
                        is AutoLoginResult.NetworkError -> {
                            state.value = SplashState.NetworkError
                            return@launch
                        }
                        is AutoLoginResult.Success -> {
                            println("✅ Token refrescado con éxito")
                        }
                    }
                }

                sessionManager.startSync()

                launch {
                    try{
                        notificationRepository.saveToken()
                    } catch (e: Exception) {
                        println("Error actualizando token en Splash : ${e.message}")
                    }
                }

                val persona = withTimeoutOrNull(10000) {
                    sessionManager.personaFlow().filterNotNull().first()
                }

                state.value = when {
                    persona != null -> SplashState.GoToMain
                    else -> SplashState.GoToRegister(userId)
                }

            } catch (e: Exception) {
                println("❌ Error crítico en SplashScreen: ${e.message}")
                clearAndGoLogin()
            }
        }
    }

    fun retry() {
        state.value = SplashState.Loading
        initialize()
    }

    private suspend fun clearAndGoLogin() {
        sessionManager.clearSession()
        delay(1000)
        state.value = SplashState.GoToLogin
    }
}