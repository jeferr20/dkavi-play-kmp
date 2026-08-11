package pe.breaker.dkaviplay.data.remote

import pe.breaker.dkaviplay.domain.model.UserSession

sealed class AutoLoginResult {
    data class Success(val session: UserSession) : AutoLoginResult()
    object InvalidToken : AutoLoginResult()
    data class NetworkError(val message: String) : AutoLoginResult()
}