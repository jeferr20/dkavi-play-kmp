package pe.breaker.dkaviplay.presentation.screen.splash

sealed class SplashState {
    object Loading : SplashState()
    object NetworkError : SplashState()
    object GoToLogin : SplashState()
    object GoToMain : SplashState()
}
