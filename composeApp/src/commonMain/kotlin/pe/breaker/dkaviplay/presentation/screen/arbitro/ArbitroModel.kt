package pe.breaker.dkaviplay.presentation.screen.arbitro

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.core.data.remote.dto.ResultadosPartidasDTO
import pe.breaker.dkaviplay.core.data.util.UserSessionManager
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.core.domain.model.ReservaEstado
import pe.breaker.dkaviplay.core.domain.model.TipoJuego
import pe.breaker.dkaviplay.core.domain.repository.JuegoRepository
import pe.breaker.dkaviplay.core.domain.repository.ReservaRepository

class ArbitroModel(
    private val reservasRepository: ReservaRepository,
    private val juegoRepository: JuegoRepository,
    private val sessionManager: UserSessionManager
) : StateScreenModel<ArbitroState>(ArbitroState()) {

    fun buscarReserva(reservaUid: String) {
        if (state.value.isLoading) return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorEscaneoMessage = null) }

            reservasRepository.getReservaById(reservaUid)
                .onSuccess { reserva ->
                    if (reserva.creadorUid == sessionManager.getUserUid() || reserva.retadoUid == sessionManager.getUserUid()) {
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                errorEscaneoMessage = "No puedes arbitrar tu propia mesa"
                            )
                        }
                    } else if (reserva.estadoInt == ReservaEstado.FINALIZADO.id) {
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                errorEscaneoMessage = "La partida ya ha sido finalizada"
                            )
                        }
                    } else {
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                reserva = reserva,
                                step = ArbitroStep.REGISTERING,
                                errorEscaneoMessage = null
                            )
                        }
                    }
                }
                .onFailure { error ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorEscaneoMessage = error.message
                                ?: "Mesa no encontrada o código inválido"
                        )
                    }
                }
        }
    }

    fun resetError() {
        mutableState.update {
            it.copy(
                errorEscaneoMessage = null,
                isLoading = false,
                step = ArbitroStep.SCANNING // Volvemos al paso inicial
            )
        }
    }

    fun resetErrorResultados() {
        mutableState.update {
            it.copy(
                errorResultadosMessage = null,
                isLoading = false
            )
        }
    }

    fun onActualizarResultado(setIndex: Int, ganadorIndex: Int) {
        val reserva = state.value.reserva ?: return
        val tipo = TipoJuego.fromString(reserva.tipoJuego)

        val nuevosResultados = state.value.resultados.toMutableList()
        nuevosResultados[setIndex] = ganadorIndex

        val victoriasP1 = nuevosResultados.count { it == 0 }
        val victoriasP2 = nuevosResultados.count { it == 1 }

        // Limpieza de sets si ya hay ganador (ej: 2-0 en Pool)
        if (tipo.esVictoria(victoriasP1) || tipo.esVictoria(victoriasP2)) {
            for (i in (setIndex + 1) until tipo.setsMaximos) {
                nuevosResultados[i] = null
            }
        }

        mutableState.update { it.copy(resultados = nuevosResultados) }
    }

    private fun mapearPartidas(
        resultados: List<Int?>,
        reserva: Reserva
    ): List<ResultadosPartidasDTO> {
        val tipo = TipoJuego.fromString(reserva.tipoJuego)

        return List(tipo.setsMaximos) { index ->
            val ganadorIndex = resultados.getOrNull(index)

            ResultadosPartidasDTO(
                numero = index + 1,
                ganadorId = when (ganadorIndex) {
                    0 -> reserva.creadorUid
                    1 -> reserva.retadoUid
                    else -> ""
                }
            )
        }
    }

    fun enviarResultados() {
        val reserva = state.value.reserva ?: return
        val resultadosActuales = state.value.resultados

        if (resultadosActuales.all { it == null }) {
            mutableState.update { it.copy(errorResultadosMessage = "Debes registrar al menos un resultado.") }
            return
        }

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorResultadosMessage = null) }

            val partidasDTO = mapearPartidas(resultadosActuales, reserva)

            juegoRepository.registrarJuego(
                reserva,
                "ARBITRO",
                sessionManager.getUserUid() ?: "",
                sessionManager.getCurrentUsuario()?.usuario ?: "",
                partidasDTO
            ).onSuccess { mensaje ->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = mensaje
                    )
                }
            }
                .onFailure { error ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorResultadosMessage = error.message
                                ?: "No se pudo enviar los resultados"
                        )
                    }
                }
        }
    }
}