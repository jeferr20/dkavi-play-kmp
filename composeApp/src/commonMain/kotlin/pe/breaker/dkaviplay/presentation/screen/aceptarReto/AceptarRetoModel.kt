package pe.breaker.dkaviplay.presentation.screen.aceptarReto

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository
import pe.breaker.dkaviplay.domain.repository.ReservaRepository
import pe.breaker.dkaviplay.domain.usecase.SendNotificacionUseCase

class AceptarRetoModel(
    private val reservaId: String,
    private val reservasRepository: ReservaRepository,
    private val quickPlayRepository: QuickPlayRepository,
    private val notificacionUseCase: SendNotificacionUseCase,
    private val sessionManager: UserSessionManager
) : StateScreenModel<AceptarRetoState>(AceptarRetoState()) {

    init {
        loadData()
    }

    private fun loadData(){
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val reserva = reservasRepository.getReservaById(reservaId)

            reserva.onSuccess { reserva->
                val retadorId = reserva.creadorUid

                quickPlayRepository.getUser(retadorId).onSuccess { retador ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            retador = retador,
                            reserva = reserva,
                            isSuccess = true
                        )
                    }
                }.onFailure { error ->
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = "Error retador: ${error.message}")
                    }
                }
            }.onFailure { error ->
                mutableState.update {
                    it.copy(isLoading = false, errorMessage = "Error reserva: ${error.message}")
                }
            }
        }
    }

    fun responderReto(aceptar: Boolean){
        val reservaActual = state.value.reserva ?: return
        val nombreUsuario = sessionManager.getCurrentUsuario()?.usuario ?: "USER ACTUAL"
        val destinatarioId = reservaActual.creadorUid

        screenModelScope.launch{
            mutableState.update { it.copy(isLoading = true) }

            val result = reservasRepository.responderReto(
                reservaId = reservaId,
                aceptar = aceptar,
                mesa = state.value.reserva?.mesa ?: "",
                sedeUid = state.value.reserva?.sedeUid ?: ""
            )

            result.onSuccess { rpta->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = if (aceptar) rpta else "Reto rechazado"
                    )
                }

                launch {
                    val titulo = if (aceptar) "¡Reto Aceptado!" else "Reto Rechazado"
                    val mensaje = if (aceptar)
                        "$nombreUsuario ha aceptado tu desafío en ${reservaActual.sede}."
                    else
                        "$nombreUsuario ha rechazado tu desafío en ${reservaActual.sede}."

                    notificacionUseCase(
                        user = destinatarioId,
                        title = titulo,
                        message = mensaje,
                        accion = "GO_RESERVATIONS"
                    ).onFailure { error ->
                        println("Error enviando notificación: ${error.message}")
                    }
                }

            }.onFailure { error ->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al responder: ${error.message}",
                        isSuccess = false
                    )
                }
            }
        }
    }
}