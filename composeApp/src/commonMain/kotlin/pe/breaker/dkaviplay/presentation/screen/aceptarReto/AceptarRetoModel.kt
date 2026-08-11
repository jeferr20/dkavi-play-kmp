package pe.breaker.dkaviplay.presentation.screen.aceptarReto

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.usecase.reserva.GetReservationByIdUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.ResponserRetoUseCase
import pe.breaker.dkaviplay.domain.usecase.usuarioQuickPlay.GetUsuarioQuickPlayUseCase

class AceptarRetoModel(
    private val reservaId: String,
    private val getReservationbyIdUseCase: GetReservationByIdUseCase,
    private val getUsuarioQuickPlayUseCase: GetUsuarioQuickPlayUseCase,
    private val responserRetoUseCase: ResponserRetoUseCase
) : StateScreenModel<AceptarRetoState>(AceptarRetoState()) {

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            getReservationbyIdUseCase(reservaId)
                .onSuccess { reserva ->
                    getUsuarioQuickPlayUseCase(reserva.creadorUid)
                        .onSuccess { retador ->
                            mutableState.update {
                                it.copy(
                                    isLoading = false,
                                    retador = retador,
                                    reserva = reserva,
                                    isSuccess = true
                                )
                            }
                        }
                        .onFailure { error ->
                            mutableState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Error retador: ${error.message}"
                                )
                            }
                        }
                }
                .onFailure { error ->
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = "Error reserva: ${error.message}")
                    }
                }
        }
    }

    fun responderReto(aceptar: Boolean) {
        val reservaActual = state.value.reserva ?: return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            responserRetoUseCase(reservaActual, aceptar).onSuccess { rpta ->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = rpta
                    )
                }
            }.onFailure { error ->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al responder: ${error.message}",
                        isSuccess = false
                    )
                }
            }
        }
    }
}