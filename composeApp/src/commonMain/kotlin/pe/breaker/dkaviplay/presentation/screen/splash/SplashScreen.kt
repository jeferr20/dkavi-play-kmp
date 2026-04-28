package pe.breaker.dkaviplay.presentation.screen.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.presentation.components.dialog.NoNetworkDialog
import pe.breaker.dkaviplay.presentation.screen.login.LoginScreen
import pe.breaker.dkaviplay.presentation.screen.mainContainer.MainContainerScreen
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosScreen
import pe.breaker.dkaviplay.presentation.screen.splash.components.SplashLayout

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<SplashModel>()
        val state by screenModel.state.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.initialize()
        }

        LaunchedEffect(state) {
            when (val s = state) {
                is SplashState.GoToLogin ->
                    navigator.replaceAll(LoginScreen())
                is SplashState.GoToMain ->
                    navigator.replaceAll(MainContainerScreen())
                is SplashState.GoToRegister ->
                    navigator.replaceAll(RegisterDatosScreen(false, s.userId))
                else -> Unit
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            SplashLayout()
            if (state is SplashState.NetworkError) {
                NoNetworkDialog(onRetry = { screenModel.retry() })
            }
        }
    }
}