package pe.breaker.dkaviplay.presentation.screen.map

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.repository.SedeRepository

class MapTabModel(
    private val sedeRepository: SedeRepository,
) : StateScreenModel<MapTabState>(MapTabState()) {

    private var mesasJob: Job? = null
    private var sedesJob: Job? = null

    init {
        observeSedes()
    }

    private fun observeSedes() {
        if (sedesJob?.isActive == true) return

        sedesJob = screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null, isSuccess = false) }

            sedeRepository.getSedes()
                .catch { error ->
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = error.message, isSuccess = false)
                    }
                }
                .collect { result ->
                    result.onSuccess { sedes ->
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                sedes = sedes,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                    }.onFailure { error ->
                        mutableState.update {
                            it.copy(isLoading = false, errorMessage = error.message, isSuccess = false)
                        }
                    }
                }
        }
    }

    fun listenToMesas(sedeUid: String) {
        mesasJob?.cancel()
        mesasJob = screenModelScope.launch {
            sedeRepository.getMesasSede(sedeUid)
                .catch { /* manejar error silencioso o loguear */ }
                .collect { ocupacion ->
                    mutableState.update { it.copy(ocupacionActual = ocupacion) }
                }
        }
    }

    fun stopListeningMesas() {
        mesasJob?.cancel()
        mesasJob = null
        mutableState.update { it.copy(ocupacionActual = "0/0") }
    }

    fun retry() {
        observeSedes()
    }

    override fun onDispose() {
        sedesJob?.cancel()
        mesasJob?.cancel()
        super.onDispose()
    }
}