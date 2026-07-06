package pe.breaker.dkaviplay.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.SessionSyncManager
import pe.breaker.dkaviplay.domain.model.GlobalEvent
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio

class GlobalUiManager(
    private val sessionSyncManager: SessionSyncManager
) : ScreenModel {

    var uiState by mutableStateOf<UiFlowState>(UiFlowState.Idle)
        private set

    private var premioPendiente: DetallePremio? = null

    init {
        observeGlobalEvents()
    }

    private fun observeGlobalEvents() {
        screenModelScope.launch {
            sessionSyncManager.globalEvent.collect { event ->
                reduce(event)
            }
        }
    }

    private fun reduce(event: GlobalEvent) {
        when (event) {
            is GlobalEvent.LevelUp -> {
                uiState = UiFlowState.ShowingLevelUp(event.nuevoRango)
            }

            is GlobalEvent.ItemGained -> {
                when (uiState) {

                    is UiFlowState.ShowingLevelUp -> {
                        premioPendiente = event.premio
                    }

                    is UiFlowState.WaitingReward -> {
                        uiState = UiFlowState.ShowingReward(event.premio)
                    }

                    else -> {
                        uiState = UiFlowState.ShowingReward(event.premio)
                    }
                }
            }
        }
    }

    fun onLevelUpDismiss() {
        val pending = premioPendiente
        premioPendiente = null

        uiState = if (pending != null) {
            UiFlowState.ShowingReward(pending)
        } else {
            startWaitingReward()
            UiFlowState.WaitingReward
        }
    }

    private fun startWaitingReward() {
        screenModelScope.launch {
            delay(10000)
            if (uiState is UiFlowState.WaitingReward) {
                uiState = UiFlowState.Idle
            }
        }
    }

    fun dismissReward() {
        uiState = UiFlowState.Idle
    }
}