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
        // 1. Cargar estado de sesión local
        sessionManager.loadSession()

        val token = sessionManager.getToken()
        val userId = sessionManager.getUserUid()
        val internalId = sessionManager.getUserId()

        println("DEBUG: Iniciando CheckSessionUseCase")
        println("DEBUG: Token presente: ${!token.isNullOrBlank()}")
        println("DEBUG: UserUID presente: ${!userId.isNullOrBlank()}")
        println("DEBUG: InternalID: $internalId")

        // Si no hay sesión local, forzamos Login
        if (token.isNullOrBlank() || userId.isNullOrBlank() || internalId == null) {
            println("DEBUG: Sesión local incompleta. Abortando.")
            return SessionCheckResult.NoSession
        }

        // 2. Si el token de la API está vencido, ejecutamos autoLogin
        if (isTokenExpired(token)) {
            when (val result = authRepository.autoLogin(token)) {
                is AutoLoginResult.InvalidToken -> return SessionCheckResult.NoSession
                is AutoLoginResult.NetworkError -> return SessionCheckResult.NetworkError(result.message)
                is AutoLoginResult.Success -> {
                    // autoLogin YA importó las sesiones en Supabase y Firebase dentro del Repository
                }
            }
        } else {
            // 3. Si el token local AÚN ES VÁLIDO, importamos las sesiones guardadas en los SDKs
            importExistingSdksSessions()
        }

        // 4. Sincronización de datos iniciales del juego/app
        val syncExitoso = sessionSyncManager.awaitFirstSync()
        if (!syncExitoso) {
            return SessionCheckResult.NetworkError("No se pudieron cargar tus datos de usuario.")
        }

        sessionSyncManager.startSync()
        return SessionCheckResult.Authenticated(userId)
    }

    /**
     * Importa las sesiones existentes en Supabase y Firebase solo cuando no fue necesario hacer autoLogin
     */
    private suspend fun importExistingSdksSessions() {
        val tokenParaSupabase = sessionManager.getSupabaseToken()
        val refreshParaSupabase = sessionManager.getSupabaseRefreshToken()

        if (!tokenParaSupabase.isNullOrBlank()) {
            try {
                supabaseClient.auth.importSession(
                    io.github.jan.supabase.auth.user.UserSession(
                        accessToken = tokenParaSupabase,
                        refreshToken = refreshParaSupabase ?: "",
                        expiresIn = 3600,
                        tokenType = "bearer",
                        user = null
                    )
                )
            } catch (e: Exception) {
                println("⚠️ Error al importar sesión guardada en Supabase: ${e.message}")
            }
        }

        val tokenParaFirebase = sessionManager.getFirebaseToken()
        if (!tokenParaFirebase.isNullOrBlank()) {
            try {
                firebaseAuth.signInWithCustomToken(tokenParaFirebase)
            } catch (e: Exception) {
                println("⚠️ Error al autenticar Firebase: ${e.message}")
            }
        }
    }
}