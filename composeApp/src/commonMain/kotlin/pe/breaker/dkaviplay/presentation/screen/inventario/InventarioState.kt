package pe.breaker.dkaviplay.presentation.screen.inventario

import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio

data class InventarioState (
    val items: List<DetallePremio> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)