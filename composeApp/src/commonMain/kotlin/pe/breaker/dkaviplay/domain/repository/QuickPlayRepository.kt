package pe.breaker.dkaviplay.domain.repository

import pe.breaker.dkaviplay.domain.model.UserQuick

interface QuickPlayRepository {
    suspend fun searchUsers(userToSearch: String) : Result<List<UserQuick>>
    suspend fun getUser(userUid: String) : Result<UserQuick>
}