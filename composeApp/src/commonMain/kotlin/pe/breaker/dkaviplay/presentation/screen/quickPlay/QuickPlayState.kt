package pe.breaker.dkaviplay.presentation.screen.quickPlay

import pe.breaker.dkaviplay.domain.model.UserQuick

data class QuickPlayState (
    val userToSearch: String? = null,
    val sedeUid: String? = null,
    val userToSearchError: String? = null,

    val usersFound: List<UserQuick> = emptyList(),

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)