package pe.breaker.dkaviplay.presentation.screen.reservas

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.repository.TimeRepository
import pe.breaker.dkaviplay.domain.usecase.EliminarReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.GetReservasUseCase
import pe.breaker.dkaviplay.util.DateTimeFormatter

class ReservasModel(
    private val sessionManager: UserSessionManager,
    private val getReservasUseCase: GetReservasUseCase,
    private val eliminarReservaUseCase : EliminarReservaUseCase,
    private val timeRepository: TimeRepository,
    private val dateTimeFormatter: DateTimeFormatter
) : StateScreenModel<ReservasState>(ReservasState()){

    private var reservasJob: Job? = null
    private var allReservas: List<Reserva> = emptyList()

    init {
        val uid = sessionManager.getUserUid() ?: ""
        mutableState.update { it.copy(currentUserUid = uid) }
        listenToReservas(uid)
    }

    private fun listenToReservas(uid: String) {
        reservasJob?.cancel()
        reservasJob = screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }

            getReservasUseCase(uid)
                .catch { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
                .collect { listaReservas ->
                    allReservas = listaReservas
                    updateFilteredList()
                }
        }
    }

    private suspend fun updateFilteredList() {
        val now = timeRepository.getServerTime()
        val currentFilter = state.value.currentFilter
        val uid = state.value.currentUserUid

        val filtradas = when (currentFilter) {
            ReservaFilter.TODAS -> allReservas
            ReservaFilter.ACTUALES -> allReservas.filter { reserva ->
                val finInstant = dateTimeFormatter.parseIsoToInstant(reserva.fechaFin)
                finInstant != null && finInstant > now
            }.sortedWith(
                compareByDescending<Reserva> {
                    it.esperandoConfirmacion && it.userPendienteUid == uid
                }.thenBy {
                    dateTimeFormatter.parseIsoToInstant(it.fechaInicio)
                }
            )

            ReservaFilter.HISTORIAL -> allReservas.filter { reserva ->
                val finInstant = dateTimeFormatter.parseIsoToInstant(reserva.fechaFin)
                finInstant != null && finInstant <= now
            }.sortedByDescending {
                dateTimeFormatter.parseIsoToInstant(it.fechaInicio)
            }
        }

        // 💡 Guardamos tanto las reservas como el tiempo seguro de este ciclo en el estado
        mutableState.update { it.copy(
            reservas = filtradas,
            serverTime = now,
            isLoading = false,
            isSuccess = true
        ) }
    }

    fun refresh() {
        val uid = sessionManager.getUserUid() ?: ""
        if (uid.isNotEmpty()) listenToReservas(uid)
    }

    fun eliminarReserva(reservaUid: String) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            eliminarReservaUseCase(reservaUid)
                .onFailure { error ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudo eliminar: ${error.message}"
                        )
                    }
                }
        }
    }

    fun clearError() = mutableState.update { it.copy(errorMessage = null) }

    fun applyFilter(filter: ReservaFilter) {
        mutableState.update { it.copy(currentFilter = filter, isLoading = true) }
        screenModelScope.launch {
            updateFilteredList()
        }
    }

    suspend fun validarHoraInicio(reserva: Reserva): Boolean {
        mutableState.update { it.copy(isLoading = true, errorMessage = null) }

        return try {
            val now = timeRepository.getServerTime()
            val inicio = dateTimeFormatter.parseIsoToInstant(reserva.fechaInicio)
            val fin = dateTimeFormatter.parseIsoToInstant(reserva.fechaFin)

            if (inicio == null || fin == null) {
                mutableState.update { it.copy(isLoading = false, errorMessage = "Error en el formato de la reserva") }
                return false
            }

            val inicioConMargen = inicio.minus(1, DateTimeUnit.MINUTE)

            when {
                now < inicioConMargen -> {
                    val horaFormateada = dateTimeFormatter.formatTimeToHHMM(reserva.fechaInicio)
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "La partida empieza a las $horaFormateada. ¡Paciencia, campeón!"
                        )
                    }
                    false
                }
                now > fin -> {
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = "Esta reserva ya expiró.")
                    }
                    false
                }
                else -> {
                    mutableState.update { it.copy(isLoading = false) }
                    true
                }
            }
        } catch (e: Exception) {
            mutableState.update { it.copy(isLoading = false, errorMessage = "Error al validar la hora") }
            false
        }
    }
}