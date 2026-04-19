package pe.breaker.dkaviplay.data.provider

interface FirebaseAuthProvider {
    suspend fun signInWithCustomToken(token: String): String
    suspend fun signOut()
    fun getCurrentUserId(): String?
    fun isAuthenticated(): Boolean
}