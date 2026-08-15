package pe.breaker.dkaviplay.presentation.screen.reservas

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.repository.TimeRepository
import pe.breaker.dkaviplay.domain.usecase.EliminarReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.GetReservasUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.RefreshReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.ValidarHoraReservaUseCase
import pe.breaker.dkaviplay.util.DateTimeFormatter

class ReservasModel(
    private val sessionManager: UserSessionManager,
    private val getReservasUseCase: GetReservasUseCase,
    private val refreshReservaUseCase : RefreshReservaUseCase,
    private val eliminarReservaUseCase: EliminarReservaUseCase,
    private val validarHoraReservaUseCase: ValidarHoraReservaUseCase,
    private val timeRepository: TimeRepository,
    private val dateTimeFormatter: DateTimeFormatter
) : StateScreenModel<ReservasState>(ReservasState()) {

    private var reservasJob: Job? = null
    private var allReservas: List<Reserva> = emptyList()

    init{
        cargarSesionYEscuchar()
    }

    fun cargarSesionYEscuchar() {
        val uid = sessionManager.getUserUid().orEmpty()
        mutableState.update { it.copy(currentUserUid = uid) }
        if (uid.isNotEmpty()) {
            listenToReservas(uid)
        }
    }

    fun refresh() {
        val uid = sessionManager.getUserUid().orEmpty()
        mutableState.update { it.copy(currentUserUid = uid) }

        if (uid.isNotEmpty()) {
            screenModelScope.launch {
                mutableState.update { it.copy(isLoading = true, errorMessage = null) }
                runCatching {
                    refreshReservaUseCase(uid)
                }.onFailure { error ->
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Error al actualizar")
                    }
                }
            }
        }
    }

    private fun listenToReservas(uid: String) {
        reservasJob?.cancel()
//        if (reservasJob?.isActive == true) return
        reservasJob = screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            getReservasUseCase(uid)
                .catch { error ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al obtener las reservas."
                        )
                    }
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
        val uid = sessionManager.getUserUid().orEmpty()

        val filtradas = when (currentFilter) {
            ReservaFilter.TODAS -> allReservas

            ReservaFilter.ACTUALES -> allReservas.filter { reserva ->
                val finInstant = dateTimeFormatter.parseIsoToInstant(reserva.fechaFin)
                // 💡 Si no tiene fechaFin (finInstant == null), asumimos que está ACTIVA/ABIERTA
                finInstant == null || finInstant > now
            }.sortedWith(
                compareByDescending<Reserva> {
                    it.esperandoConfirmacion && it.userPendienteUid == uid
                }.thenBy {
                    dateTimeFormatter.parseIsoToInstant(it.fechaInicio)
                }
            )

            ReservaFilter.HISTORIAL -> allReservas.filter { reserva ->
                val finInstant = dateTimeFormatter.parseIsoToInstant(reserva.fechaFin)
                // 💡 Solo entra al historial si TIENE fechaFin Y esta ya pasó
                finInstant != null && finInstant <= now
            }.sortedByDescending {
                dateTimeFormatter.parseIsoToInstant(it.fechaInicio)
            }
        }

        mutableState.update {
            it.copy(
                currentUserUid = uid,
                reservas = filtradas,
                serverTime = now,
                isLoading = false,
                isSuccess = true
            )
        }
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
            val result = validarHoraReservaUseCase(reserva.reservaUid)

            var esValido = false

            result.onSuccess { data ->
                if (data.valido) {
                    mutableState.update { it.copy(isLoading = false) }
                    esValido = true
                } else {
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = data.mensaje)
                    }
                    esValido = false
                }
            }.onFailure { error ->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al validar la hora"
                    )
                }
                esValido = false
            }

            esValido
        } catch (e: Exception) {
            mutableState.update {
                it.copy(isLoading = false, errorMessage = "Error inesperado al validar la hora")
            }
            false
        }
    }
}