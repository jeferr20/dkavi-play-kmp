package pe.breaker.dkaviplay.presentation.screen.quickPlayDetail

import pe.breaker.dkaviplay.core.domain.model.UserQuick

data class QuickPlayDetailState(
    var usuario: UserQuick? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)