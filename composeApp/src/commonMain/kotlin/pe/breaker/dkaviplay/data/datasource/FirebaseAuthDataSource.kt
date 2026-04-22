package pe.breaker.dkaviplay.data.datasource

interface FirebaseAuthDataSource {
    suspend fun signInWithCustomToken(token: String): String  // retorna uid
    suspend fun signOut()
    fun getCurrentUserUid(): String?
}