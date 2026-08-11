package pe.breaker.dkaviplay.presentation.screen.map

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.repository.SedeRepository
import pe.breaker.dkaviplay.util.obtenerCoordenadasPorUbigeo

class MapTabModel(
    private val sedeRepository: SedeRepository,
    private val userSessionManager: UserSessionManager
) : StateScreenModel<MapTabState>(MapTabState()) {

    private var mesasJob: Job? = null
    private var sedesJob: Job? = null

    init {
        observeSedes()
    }

    private fun observeSedes() {
        if (sedesJob?.isActive == true) return

        sedesJob = screenModelScope.launch {
            mutableState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                )
            }
            val usuarioDepartamento = userSessionManager.getCurrentUsuario()?.departamento

            sedeRepository.getSedes()
                .catch { error ->
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = error.message, isSuccess = false)
                    }
                }
                .collect { result ->
                    result.onSuccess { sedes ->
                        val centroCalculado = state.value.mapCenter ?: if (sedes.isNotEmpty()) {
                            val avgLat = sedes.map { it.latitud }.average()
                            val avgLng = sedes.map { it.longitud }.average()
                            Pair(avgLat, avgLng)
                        } else {
                            obtenerCoordenadasPorUbigeo(usuarioDepartamento) ?: Pair(-12.0464, -77.0428)
                        }

                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                sedes = sedes,
                                mapCenter = centroCalculado,
                                isSuccess = true,
                                errorMessage = null
                            )
                        }
                    }.onFailure { error ->
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
            try {
                val ocupacion = sedeRepository.getMesasSede(sedeUid)
                mutableState.update { it.copy(ocupacionActual = ocupacion) }
            } catch (e: Exception) {
                mutableState.update { it.copy(ocupacionActual = "0/0") }
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