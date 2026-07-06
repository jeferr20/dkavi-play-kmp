package pe.breaker.dkaviplay.presentation.screen.moneda

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.http.*
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.usecase.GetMensajeCompraUseCase

class MonedaModel(
    private val sessionManager: UserSessionManager,
    private val getMensajeCompraUseCase: GetMensajeCompraUseCase
) : StateScreenModel<MonedaState>(MonedaState()) {

    fun procesarCompra(cantidadMonedas: Int) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val usuarioActual = sessionManager.getCurrentUsuario()?.usuario ?: "Usuario KMP"
                val textoMensaje = getMensajeCompraUseCase(cantidadMonedas, usuarioActual)

                val telefonoSoporte = "51959817861"

                val whatsappUrl = URLBuilder("https://wa.me/$telefonoSoporte").apply {
                    parameters.append("text", textoMensaje)
                }.buildString()

                mutableState.update {
                    it.copy(isLoading = false, actionEvent = MonedaUiEvent.OpenWhatsApp(whatsappUrl))
                }
            } catch (e: Exception) {
                mutableState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Error al procesar la compra")
                }
            }
        }
    }

    fun consumeActionEvent() {
        mutableState.update { it.copy(actionEvent = null) }
    }
}