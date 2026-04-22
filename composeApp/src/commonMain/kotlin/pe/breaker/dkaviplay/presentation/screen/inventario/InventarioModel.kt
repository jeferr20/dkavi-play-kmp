package pe.breaker.dkaviplay.presentation.screen.inventario

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.data.remote.firebase.InventarioFirebase
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.inventory.PremiosRegistry
import pe.breaker.dkaviplay.domain.model.inventory.TipoPremio
import pe.breaker.dkaviplay.domain.repository.UsuarioRepository

class InventarioModel(
    private val tipoItem: TipoPremio,
    private val userSessionManager: UserSessionManager,
    private val usuarioRepository: UsuarioRepository,
) : StateScreenModel<InventarioState>(InventarioState()) {

    init {
        escucharInventario()
    }

    private fun escucharInventario() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }

            userSessionManager.getCurrentUsuarioFlow().collect { usuario ->
                if (usuario != null) {
                    val inventarioFirebase = usuario.inventarioJson?.let { jsonStr ->
                        try {
                            Json.decodeFromString<List<InventarioFirebase>>(jsonStr)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()

                    val itemsBase = PremiosRegistry.obtenerByTipo(tipoItem)

                    val itemsConCantidad = itemsBase.map { itemBase ->
                        val itemEnFb = inventarioFirebase.find { it.id == itemBase.id }
                        itemBase.copy(
                            cantidad = itemEnFb?.cantidad ?: 0
                        )
                    }

                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            items = itemsConCantidad,
                            isSuccess = true
                        )
                    }
                } else {
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = "No se encontró el usuario")
                    }
                }
            }
        }
    }

    fun actualizarRecompensas(reserva: Reserva, itemId: Int) {
        screenModelScope.launch {
            mutableState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )
            }
            val userId = userSessionManager.getUserUid() ?: ""
            val soyElCreador = reserva.creadorUid == userId

            usuarioRepository.usarRecompensas(
                userId = userId,
                itemId = itemId,
                reservaId = reserva.reservaUid,
                isCreador = soyElCreador
            ).onSuccess { message ->
                mutableState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = message
                    )
                }
            }
                .onFailure { error ->
                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al usar item"
                        )
                    }
                }
        }
    }

    fun clearSuccessMessage() {
        mutableState.update { it.copy(successMessage = null) }
    }

    fun resetError() {
        mutableState.update { it.copy(errorMessage = null) }
    }
}