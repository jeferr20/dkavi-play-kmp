package pe.breaker.dkaviplay.data.provider

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthProviderAndroid : FirebaseAuthProvider {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun signInWithCustomToken(token: String): String {
        val result = auth.signInWithCustomToken(token).await()
        return result.user?.uid ?: throw Exception("UID nulo tras login")
    }

    override suspend fun signOut() = auth.signOut()

    override fun getCurrentUserId(): String? = auth.currentUser?.uid

    override fun isAuthenticated(): Boolean = auth.currentUser != null
}