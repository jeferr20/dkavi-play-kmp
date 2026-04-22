package pe.breaker.dkaviplay.data.remote

sealed class AutoLoginResult {
    object Success : AutoLoginResult()
    object InvalidToken : AutoLoginResult() // 401 Unauthorized
    data class NetworkError(val message: String) : AutoLoginResult()
}