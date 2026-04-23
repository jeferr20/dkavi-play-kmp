package pe.breaker.dkaviplay.presentation.screen.retoIniciado

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.ReservaEstado
import pe.breaker.dkaviplay.domain.repository.JuegoRepository
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository
import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class RetoIniciadoModel(
    private val reservaId: String,
    private val reservasRepository: ReservaRepository,
    private val quickPlayRepository: QuickPlayRepository
) : StateScreenModel<RetoIniciadoState>(RetoIniciadoState()) {

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            reservasRepository.getReservaById(reservaId)
                .onSuccess { reserva ->
                    manejarCambioEstado(reserva)

                    cargarUsuarios(reserva)
                }
                .onFailure { error ->
                    onError("No se encontró la reserva: ${error.message}")
                }
        }
    }

    private suspend fun manejarCambioEstado(reserva: Reserva) {
        val estadoActual = ReservaEstado.fromId(reserva.estadoInt)
        if (estadoActual == ReservaEstado.APROBADO) {
            reservasRepository.updateEstadoReserva(ReservaEstado.EN_JUEGO, reserva.reservaUid)
        }
    }

    private suspend fun cargarUsuarios(reserva: Reserva) {
        try {
            coroutineScope {
                // Usamos supervisorScope si queremos que un fallo no cancele al otro
                val retadorDef = async { quickPlayRepository.getUser(reserva.creadorUid) }
                val retadoDef = async { quickPlayRepository.getUser(reserva.retadoUid) }

                val retadorRes = retadorDef.await()
                val retadoRes = retadoDef.await()

                if (retadorRes.isSuccess && retadoRes.isSuccess) {
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            reserva = reserva,
                            retador = retadorRes.getOrNull(),
                            retado = retadoRes.getOrNull(),
                            isSuccess = true
                        )
                    }
                } else {
                    onError("Error al cargar los perfiles de los jugadores")
                }
            }
        } catch (e: Exception) {
            onError("Fallo en la comunicación con el servidor: ${e.message}")
        }
    }

    private fun onError(msg: String) {
        mutableState.update { it.copy(isLoading = false, errorMessage = msg) }
    }
}