package pe.breaker.dkaviplay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.di.AppConfigManager
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.presentation.screen.splash.SplashScreen
import pe.breaker.dkaviplay.util.AppStateHandler

@Composable
@Preview
fun App() {
    val appConfigManager = koinInject<AppConfigManager>()
    val sessionManager = koinInject<UserSessionManager>()

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color =  Color.Black
        ){
            AppStateHandler(appConfigManager,sessionManager){
                Navigator(screen = SplashScreen()) { navigator ->
                    SlideTransition(navigator) { screen ->
                        screen.Content()
                    }
                }
            }
        }
    }
}