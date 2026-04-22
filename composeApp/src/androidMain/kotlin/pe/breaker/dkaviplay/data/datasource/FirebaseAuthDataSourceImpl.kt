package pe.breaker.dkaviplay.data.datasource

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSourceImpl : FirebaseAuthDataSource {
    private val auth = FirebaseAuth.getInstance()

    override suspend fun signInWithCustomToken(token: String): String {
        val result = auth.signInWithCustomToken(token).await()
        return result.user?.uid
            ?: throw IllegalStateException("Firebase: uid nulo tras login")
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUserUid(): String? = auth.currentUser?.uid
}