package pe.breaker.dkaviplay.domain.usecase.quickPlay

import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository

class SearchUsersQuickPlayUseCase(
    private val repository: QuickPlayRepository
) {
    suspend operator fun invoke(userToSearch: String): Result<List<UserQuick>> {
        val query = userToSearch.trim()
//        if (query.length < 3 || query.isNotEmpty()) {
//            return Result.failure(Exception("Escribe al menos 3 caracteres para buscar"))
//        }

        return repository.searchUsers(query)
    }
}