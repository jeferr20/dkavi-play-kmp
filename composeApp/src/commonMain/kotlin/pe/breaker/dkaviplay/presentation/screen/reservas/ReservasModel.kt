package pe.breaker.dkaviplay.presentation.screen.reservas

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.repository.TimeRepository
import pe.breaker.dkaviplay.domain.usecase.EliminarReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.GetReservasUseCase
import pe.breaker.dkaviplay.presentation.util.parseStringToLocalDateTime

class ReservasModel(
    private val sessionManager: UserSessionManager,
    private val getReservasUseCase: GetReservasUseCase,
    private val eliminarReservaUseCase : EliminarReservaUseCase,
    private val timeRepository: TimeRepository
) : StateScreenModel<ReservasState>(ReservasState()){

    private var reservasJob: Job? = null
    private var allReservas: List<Reserva> = emptyList()
    private val timeZone = TimeZone.of("America/Lima")

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
            ReservaFilter.ACTUALES -> allReservas.filter {
                val fin = parseStringToLocalDateTime(it.fechaFin).toInstant(timeZone)
                fin > now
            }.sortedWith(
                compareByDescending<Reserva> {
                    // Retos pendientes de MI confirmación
                    it.esperandoConfirmacion && it.userPendienteUid == uid
                }.thenBy {
                    //Las que están ocurriendo o más próximas a iniciar
                    parseStringToLocalDateTime(it.fechaInicio).toInstant(timeZone)
                }
            )

            ReservaFilter.HISTORIAL -> allReservas.filter {
                val fin = parseStringToLocalDateTime(it.fechaFin).toInstant(timeZone)
                fin <= now
            }.sortedByDescending {
                parseStringToLocalDateTime(it.fechaInicio).toInstant(timeZone)
            }
        }

        mutableState.update { it.copy(
            reservas = filtradas,
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
            val inicio = parseStringToLocalDateTime(reserva.fechaInicio).toInstant(timeZone)
            val fin = parseStringToLocalDateTime(reserva.fechaFin).toInstant(timeZone)

            val inicioConMargen = inicio.minus(1, DateTimeUnit.MINUTE)

            when {
                now < inicioConMargen -> {
                    val horaSolo = reserva.fechaInicio.split(" ").getOrNull(1)?.take(5) ?: reserva.fechaInicio
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "La partida empieza a las $horaSolo. ¡Paciencia, campeón!"
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