package pe.breaker.dkaviplay.core.data.datasource

interface FirebaseAuthDataSource {
    suspend fun signInWithCustomToken(token: String): String  // retorna uid
    suspend fun signOut()
    fun getCurrentUserUid(): String?
}