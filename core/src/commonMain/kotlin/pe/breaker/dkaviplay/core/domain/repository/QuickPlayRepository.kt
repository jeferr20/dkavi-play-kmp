package pe.breaker.dkaviplay.core.domain.repository

import pe.breaker.dkaviplay.core.domain.model.UserQuick

interface QuickPlayRepository {
    suspend fun searchUsers(userToSearch: String) : Result<List<UserQuick>>
    suspend fun getUser(userUid: String) : Result<UserQuick>
}