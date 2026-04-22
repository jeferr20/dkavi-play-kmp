package pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.data.remote.dto.ResultadosPartidasDTO
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.ReservaEstado
import pe.breaker.dkaviplay.domain.model.TipoJuego
import pe.breaker.dkaviplay.domain.repository.JuegoRepository
import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class AcuerdoMutuoModel(
    private val reservaUid: String,
    private val reservasRepository: ReservaRepository,
    private val juegoRepository: JuegoRepository
) : StateScreenModel<AcuerdoMutuoState>(AcuerdoMutuoState()) {

    init {
        buscarReserva()
    }

    fun buscarReserva() {
        if (state.value.isLoading) return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            reservasRepository.getReservaById(reservaUid)
                .onSuccess { reserva ->
                    if (reserva.estadoInt == ReservaEstado.FINALIZADO.id) {
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "La partida ya ha sido finalizada"
                            )
                        }
                    } else {
                        val resultadosMapeados = mapearResultadosIniciales(reserva)

                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                reserva = reserva,
                                resultados = resultadosMapeados,
                                errorMessage = null
                            )
                        }
                    }
                }
                .onFailure { error ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Mesa no encontrada o código inválido"
                        )
                    }
                }
        }
    }

    fun resetError() {
        mutableState.update {
            it.copy(
                errorMessage = null,
                isLoading = false,
            )
        }
    }

    private fun mapearResultadosIniciales(reserva: Reserva): List<Int?> {
        val tipo = TipoJuego.fromString(reserva.tipoJuego)
        val listaBase = MutableList<Int?>(tipo.setsMaximos) { null }

        reserva.partidas.forEachIndexed { index, dto ->
            if (index < listaBase.size) {
                listaBase[index] = when (dto.ganadorId) {
                    reserva.creadorUid -> 0
                    reserva.retadoUid -> 1
                    else -> null
                }
            }
        }
        return listaBase
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
            mutableState.update { it.copy(errorMessage = "Debes registrar al menos un resultado.") }
            return
        }

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            val partidasDTO = mapearPartidas(resultadosActuales, reserva)

            if (reserva.partidas.isNotEmpty()) {
                if (partidasDTO == state.value.reserva?.partidas) {
                    juegoRepository.cerrarAcuerdoMutuo(
                        reserva
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
                                    errorMessage = error.message
                                        ?: "No se pudo enviar los resultados"
                                )
                            }
                        }
                } else {
                    juegoRepository.actualizarResultados(
                        reserva,
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
                                    errorMessage = error.message
                                        ?: "No se pudo enviar los resultados"
                                )
                            }
                        }
                }
            } else {
                juegoRepository.registrarJuego(
                    reserva,
                    "MUTUO",
                    null,
                    null,
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
                                errorMessage = error.message
                                    ?: "No se pudo enviar los resultados"
                            )
                        }
                    }
            }
        }
    }
}