package pe.breaker.dkaviplay.domain.usecase

import dev.gitlive.firebase.auth.FirebaseAuth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import pe.breaker.dkaviplay.data.remote.AutoLoginResult
import pe.breaker.dkaviplay.data.util.isTokenExpired
import pe.breaker.dkaviplay.di.SessionSyncManager
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.SessionCheckResult
import pe.breaker.dkaviplay.domain.repository.AuthRepository

class CheckSessionUseCase(
    private val authRepository: AuthRepository,
    private val sessionManager: UserSessionManager,
    private val supabaseClient: SupabaseClient,
    private val firebaseAuth: FirebaseAuth,
    private val sessionSyncManager: SessionSyncManager
) {
    suspend operator fun invoke(): SessionCheckResult {
        sessionManager.loadSession()

        val token = sessionManager.getToken()
        val userId = sessionManager.getUserUid()
        val internalId = sessionManager.getUserId()
        val supabaseToken = sessionManager.getSupabaseToken()
        val firebaseToken = sessionManager.getFirebaseToken()

        if (token == null || userId == null || internalId == null) {
            return SessionCheckResult.NoSession
        }

        if (isTokenExpired(token)) {
            val isLoggedIn = authRepository.isLoggedIn()
            if (!isLoggedIn) return SessionCheckResult.NoSession

            when (val result = authRepository.autoLogin(token)) {
                is AutoLoginResult.InvalidToken -> return SessionCheckResult.NoSession
                is AutoLoginResult.NetworkError -> return SessionCheckResult.NetworkError(result.message)
                is AutoLoginResult.Success -> { /* Los nuevos tokens ya se guardaron en caché interna */ }
            }
        }

        val tokenParaSupabase = sessionManager.getSupabaseToken()
        tokenParaSupabase?.let {
            try {
                supabaseClient.auth.importSession(
                    io.github.jan.supabase.auth.user.UserSession(
                        accessToken = it,
                        refreshToken = "",
                        expiresIn = 3600,
                        tokenType = "bearer",
                        user = null
                    )
                )
            } catch (e: Exception) {
                when (authRepository.autoLogin(token)) {
                    is AutoLoginResult.Success -> {
                        val freshSupabaseToken = sessionManager.getSupabaseToken()

                        freshSupabaseToken?.let { freshToken ->
                            supabaseClient.auth.importSession(
                                io.github.jan.supabase.auth.user.UserSession(
                                    accessToken = freshToken,
                                    refreshToken = "",
                                    expiresIn = 3600,
                                    tokenType = "bearer",
                                    user = null
                                )
                            )
                        }
                    }
                    else -> return SessionCheckResult.NoSession
                }
            }
        }

        val tokenParaFirebase = sessionManager.getFirebaseToken()
        tokenParaFirebase?.let { firebaseAuth.signInWithCustomToken(it) }

        val syncExitoso = sessionSyncManager.awaitFirstSync()

        if (!syncExitoso) {
            return SessionCheckResult.NetworkError("No se pudieron cargar tus datos de usuario.")
        }

        sessionSyncManager.startSync()
        return SessionCheckResult.Authenticated(userId)
    }
}