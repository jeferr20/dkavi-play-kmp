package pe.breaker.dkaviplay.presentation.screen.quickPlay

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.usecase.SearchUsersQuickPlayUseCase

class QuickPlayModel(
    private val searchUsersQuickPlayUseCase: SearchUsersQuickPlayUseCase
) : StateScreenModel<QuickPlayState>(QuickPlayState()) {

    init {
        searchUser("")
    }

    fun searchUser(userToSearch: String) {
        screenModelScope.launch {
            mutableState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                )
            }

            searchUsersQuickPlayUseCase(userToSearch)
                .onSuccess { usuarios ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            usersFound = usuarios,
                            isSuccess = true
                        )
                    }
                }
                .onFailure { error ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error desconocido",
                            isSuccess = false
                        )
                    }
                }
        }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }
}