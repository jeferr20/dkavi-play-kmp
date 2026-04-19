package pe.breaker.dkaviplay.presentation.screen.map

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.repository.SedeRepository

class MapTabModel(
    private val sedeRepository: SedeRepository,
) : StateScreenModel<MapTabState>(MapTabState()) {

    private var mesasJob: Job? = null
    private var sedesJob: Job? = null

    init {
        getSedes()
    }

    fun getSedes() {
        sedesJob?.cancel()

        sedesJob = screenModelScope.launch {
            mutableState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                )
            }

            sedeRepository.getSedes()
                .collect { result ->
                    result
                        .onSuccess { sedes ->
                            mutableState.update {
                                it.copy(isLoading = false, sedes = sedes, isSuccess = true)
                            }
                        }
                        .onFailure { error ->
                            mutableState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = error.message,
                                    isSuccess = false
                                )
                            }
                        }
                }
        }
    }

    fun listenToMesas(sedeUid: String) {
        mesasJob?.cancel()
        mesasJob = screenModelScope.launch {
            sedeRepository.getMesasSede(sedeUid).collect { ocupacion ->
                mutableState.update { it.copy(ocupacionActual = ocupacion) }
            }
        }
    }

    fun stopListeningMesas() {
        mesasJob?.cancel()
        mutableState.update { it.copy(ocupacionActual = "0/0") }
    }
}