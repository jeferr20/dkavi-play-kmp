package pe.breaker.dkaviplay.domain.usecase

import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.util.isTokenExpired
import pe.breaker.dkaviplay.di.SessionSyncManager
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.SessionCheckResult
import pe.breaker.dkaviplay.domain.repository.AuthRepository

class CheckSessionUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager,
    private val sessionSyncManager: SessionSyncManager
) {
    suspend operator fun invoke(): SessionCheckResult {
        sessionManager.loadSession()

        val token = sessionManager.getToken()
        val userId = sessionManager.getUserUid()

        if (token == null || userId == null) {
            return SessionCheckResult.NoSession
        }

        if (isTokenExpired(token)) {
            val isLoggedIn = authRepository.isLoggedIn()
            if (!isLoggedIn) return SessionCheckResult.NoSession

            when (val result = authRepository.autoLogin(token)) {
                is AutoLoginResult.InvalidToken -> return SessionCheckResult.NoSession
                is AutoLoginResult.NetworkError -> return SessionCheckResult.NetworkError(result.message)
                is AutoLoginResult.Success -> { /* Continúa si se refrescó con éxito */ }
            }
        }

        sessionSyncManager.startSync()
        return SessionCheckResult.Authenticated(userId)
    }
}