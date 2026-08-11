package pe.breaker.dkaviplay.presentation.screen.moneda

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.data.remote.dto.request.LogMonedaDTO
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.usecase.moneda.GetMensajeCompraUseCase
import pe.breaker.dkaviplay.domain.usecase.moneda.GetTarifaMonedaUseCase
import pe.breaker.dkaviplay.domain.usecase.moneda.InsertMonedaLogUseCase
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MonedaModel(
    private val sessionManager: UserSessionManager,
    private val getMensajeCompraUseCase: GetMensajeCompraUseCase,
    private val getTarifaMonedaUseCase: GetTarifaMonedaUseCase,
    private val insertMonedaLogUseCase: InsertMonedaLogUseCase
) : StateScreenModel<MonedaState>(MonedaState()) {

    init {
        getTarifaMoneda()
    }

    fun getTarifaMoneda() {
        val sedeId = sessionManager.getCurrentUsuario()?.sedePreferencia
        sedeId?.let { sede ->
            screenModelScope.launch {
                mutableState.update {
                    it.copy(
                        isLoading = true,
                        errorMessage = null,
                        isTiendaDisponible = false,
                        numPago = ""
                    )
                }
                try {
                    getTarifaMonedaUseCase(sede.toInt())
                        .onSuccess { tarifario ->
                            val tiendaDisponible = tarifario.precioMonedas > 0.0
                            mutableState.update {
                                it.copy(
                                    isLoading = false,
                                    precioMoneda = tarifario.precioMonedas,
                                    numPago = tarifario.numeroPago,
                                    isTiendaDisponible = tiendaDisponible
                                )
                            }
                        }
                        .onFailure {
                            mutableState.update {
                                it.copy(
                                    isLoading = false,
                                    precioMoneda = 0.0,
                                    numPago = "",
                                    isTiendaDisponible = false
                                )
                            }
                        }
                } catch (e: Exception) {
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            isTiendaDisponible = false,
                            errorMessage = e.message
                                ?: "Error al obtener los precios de las monedas"
                        )
                    }
                }
            }
        }
    }

    // Cambiado: Ahora levanta el diálogo de QR calculando los datos necesarios
    fun procesarCompra(cantidadMonedas: Int) {
        val total = cantidadMonedas * state.value.precioMoneda
        val uniqueIdempotencyKey = generateUUID() // Generamos la llave para este intento específico

        mutableState.update {
            it.copy(
                montoCalculado = total,
                cantidadSeleccionada = cantidadMonedas,
                idempotencyKey = uniqueIdempotencyKey,
                showPagoDialog = true
            )
        }
    }

    // Ejecuta la redirección final a WhatsApp usando Ktor HTTP URLBuilder
    fun enviarConfirmacionWhatsApp() {
        val currentState = state.value
        val cantidad = currentState.cantidadSeleccionada
        val usuarioActual = sessionManager.getCurrentUsuario()

        val userUid = usuarioActual?.uidAuth ?: "" // UID de Supabase Auth
        val sedeId = usuarioActual?.sedePreferencia?.toIntOrNull() ?: 0

        // 1. Mostrar estado de carga antes de iniciar la transacción
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, showPagoDialog = false) }

            val logDto = LogMonedaDTO(
                cantidadMonedas = cantidad,
                monto = currentState.montoCalculado,
                sedeId = sedeId,
                idempotencyKey = currentState.idempotencyKey
            )

            // 2. Registrar el Log de Monedas primero (Operación Bloqueante del Flujo)
            insertMonedaLogUseCase(logDto)
                .onSuccess {
                    // 3. ÉXITO: Proceder a generar el mensaje y enlace de WhatsApp
                    getMensajeCompraUseCase(
                        usuario = userUid,
                        cantidadMonedas = cantidad,
                    ).onSuccess { whatsappUrl ->
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                actionEvent = MonedaUiEvent.OpenWhatsApp(whatsappUrl)
                            )
                        }
                    }.onFailure { error ->
                        mutableState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message
                                    ?: "Error al generar el mensaje de compra"
                            )
                        }
                    }
                }.onFailure { error ->
                    // 4. ERROR EN LOG: Detener el flujo y notificar al usuario
                    println("❌ Error en LogMonedas: ${error.message}")
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                                ?: "No se pudo registrar la transacción. Intente nuevamente."
                        )
                    }
                }
        }
    }

    fun ocultarPagoDialog() {
        mutableState.update { it.copy(showPagoDialog = false) }
    }

    fun consumeActionEvent() {
        mutableState.update { it.copy(actionEvent = null) }
    }

    // Helper multiplataforma sencillo para generar UUID v4 si no tienes una biblioteca dedicada
    @OptIn(ExperimentalUuidApi::class)
    private fun generateUUID(): String {
        return Uuid.random().toString()
    }
}