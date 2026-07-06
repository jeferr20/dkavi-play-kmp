package pe.breaker.dkaviplay.presentation.screen.quickPlayDetail

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.core.domain.usecase.GetUsuarioQuickPlayUseCase

class QuickPlayDetailModel(
    private val getUsuarioQuickPlayUseCase: GetUsuarioQuickPlayUseCase
) : StateScreenModel<QuickPlayDetailState>(QuickPlayDetailState()) {

    fun getUser(userUid: String) {
        if (state.value.usuario?.userUid == userUid) return

        screenModelScope.launch {
            mutableState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                )
            }

            getUsuarioQuickPlayUseCase(userUid)
                .onSuccess { usuario ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            usuario = usuario,
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