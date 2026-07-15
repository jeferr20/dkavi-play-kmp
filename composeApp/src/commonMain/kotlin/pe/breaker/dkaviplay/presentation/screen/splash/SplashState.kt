package pe.breaker.dkaviplay.presentation.screen.splash

sealed class SplashState {
    object Loading : SplashState()
    object NetworkError : SplashState()
    object GoToLogin : SplashState()
//    data class GoToRegister(val userId: String) : SplashState()
    object GoToMain : SplashState()
}
