package pe.breaker.dkaviplay.presentation.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.util.isTokenExpired
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.presentation.components.dialog.NoNetworkDialog
import pe.breaker.dkaviplay.presentation.screen.login.LoginScreen
import pe.breaker.dkaviplay.presentation.screen.mainContainer.MainContainerScreen
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosScreen
import pe.breaker.dkaviplay.presentation.screen.splash.components.SplashLayout

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
       val authRepository = koinInject<AuthRepository>()
        val sessionManager = koinInject<UserSessionManager>()
//        val notificationRepository = koinInject<NotificationRepository>()

        var showNetworkError by remember { mutableStateOf(false) }
        var retryTrigger by remember { mutableStateOf(0) }

        LaunchedEffect(retryTrigger) {
            sessionManager.loadSession()
            showNetworkError = false
            delay(500)
            try {
                val token = sessionManager.getToken()
                val userId = sessionManager.getUserUid()

                if (token == null || userId == null) {
                    navegarAlLogin(sessionManager, navigator)
                    return@LaunchedEffect
                }

                var proceedToLoad = true

                if (isTokenExpired(token)) {
                    when (authRepository.autoLogin(token)) {
                        is AutoLoginResult.InvalidToken -> {
                            navegarAlLogin(sessionManager, navigator)
                            return@LaunchedEffect
                        }
                        is AutoLoginResult.NetworkError -> {
                            showNetworkError = true
                            proceedToLoad = false
                        }
                        is AutoLoginResult.Success -> println("Token refrescado con éxito")
                    }
                }

                if (proceedToLoad){
                    sessionManager.startSync()

//                    launch {
//                        try {
//                            val token = Firebase.messaging.getToken()
//                            notificationRepository.saveToken(token)
//                        } catch (e: Exception) {
//                            println("Error actualizando token en Splash : ${e.message}")
//                        }
//                    }

                    val persona = withTimeoutOrNull(10000) {
                        sessionManager.personaFlow().filterNotNull().first()
                    }

                    println("PERSONA: ${persona?.personaUid}")
                    when {
                        persona != null -> navigator.replaceAll(MainContainerScreen())
                        else -> {
                            sessionManager.clearSession()
                            navigator.replaceAll(RegisterDatosScreen(false, userId))
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error crítico en SplashScreen: ${e.message}")
                navegarAlLogin(sessionManager, navigator)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            SplashLayout()

            if (showNetworkError) {
                NoNetworkDialog(
                    onRetry = { retryTrigger++ }
                )
            }
        }
    }

    private suspend fun navegarAlLogin(sessionManager: UserSessionManager, navigator: Navigator) {
        sessionManager.clearSession()
        delay(1000)
        navigator.replaceAll(LoginScreen())
    }
}