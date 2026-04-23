package pe.breaker.dkaviplay.presentation.screen.resultadosPartida

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository
import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class ResultadoPartidaModel(
    private val reservaId: String,
    private val sessionManager: UserSessionManager,
    private val reservasRepository: ReservaRepository,
    private val quickPlayRepository: QuickPlayRepository
) : StateScreenModel<ResultadoPartidaState>(ResultadoPartidaState()) {

    init {
        val currentUserId = sessionManager.getUserUid()
        mutableState.update { it.copy(currentUserId = currentUserId) }
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            reservasRepository.getReservaById(reservaId)
                .onSuccess { reserva ->
                    cargarUsuarios(reserva)
                }
                .onFailure { error ->
                    onError("No se encontró la reserva: ${error.message}")
                }
        }
    }

    private suspend fun cargarUsuarios(reserva: Reserva) {
        try {
            coroutineScope {
                val retadorDeferred = async { quickPlayRepository.getUser(reserva.creadorUid) }
                val retadoDeferred = async { quickPlayRepository.getUser(reserva.retadoUid) }

                val retador = retadorDeferred.await().getOrThrow()
                val retado = retadoDeferred.await().getOrThrow()

                val esCreadorGanador = reserva.ganadorUid == retador.userUid

                val ganador = if (esCreadorGanador) retador else retado
                val perdedor = if (esCreadorGanador) retado else retador

                mutableState.update {
                    it.copy(
                        isLoading = false,
                        reserva = reserva,
                        ganador = ganador,
                        perdedor = perdedor,
                        isSuccess = true
                    )
                }
            }
        } catch (e: Exception) {
            onError("Fallo al cargar la información del juego: ${e.message}")
        }
    }

    private fun onError(msg: String) {
        mutableState.update { it.copy(isLoading = false, errorMessage = msg) }
    }
}