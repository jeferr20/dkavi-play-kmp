package pe.breaker.dkaviplay.util

import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.core.domain.model.rango.Rango

sealed class UiFlowState {
    object Idle : UiFlowState()
    data class ShowingLevelUp(val rango: Rango) : UiFlowState()
    data class ShowingReward(val premio: DetallePremio) : UiFlowState()
    object WaitingReward : UiFlowState()
}