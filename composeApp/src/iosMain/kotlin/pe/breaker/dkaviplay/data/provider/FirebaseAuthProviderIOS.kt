package pe.breaker.dkaviplay.data.provider

//import cocoapods.FirebaseAuth.FIRAuth
//import kotlinx.cinterop.ExperimentalForeignApi
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//import kotlin.coroutines.suspendCoroutine
//
//@OptIn(ExperimentalForeignApi::class)
//class FirebaseAuthProviderIOS : FirebaseAuthProvider {
//
//    override suspend fun signInWithCustomToken(token: String): String =
//        suspendCoroutine { cont ->
//            FIRAuth.auth().signInWithCustomToken(token) { result, error ->
//                if (error != null) {
//                    cont.resumeWithException(Exception(error.localizedDescription))
//                } else {
//                    cont.resume(result?.user()?.uid() ?: "")
//                }
//            }
//        }
//
//    @OptIn(ExperimentalForeignApi::class)
//    override suspend fun signOut() {
//        FIRAuth.auth().signOut(null)
//    }
//
//    override fun getCurrentUserId(): String? =
//        FIRAuth.auth().currentUser()?.uid()
//
//    override fun isAuthenticated(): Boolean =
//        FIRAuth.auth().currentUser() != null
//}