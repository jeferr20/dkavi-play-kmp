package pe.breaker.dkaviplay.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.SessionSyncManager
import pe.breaker.dkaviplay.domain.model.GlobalEvent
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio

class GlobalUiManager(
    private val sessionSyncManager: SessionSyncManager
) : ScreenModel {

    var uiState by mutableStateOf<UiFlowState>(UiFlowState.Idle)
        private set

    private val rewardQueue = mutableListOf<DetallePremio>()
    private var levelUpPendiente: pe.breaker.dkaviplay.domain.model.rango.Rango? = null

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
                if (uiState is UiFlowState.Idle) {
                    uiState = UiFlowState.ShowingLevelUp(event.nuevoRango)
                } else {
                    levelUpPendiente = event.nuevoRango
                }
            }

            is GlobalEvent.ItemGained -> {
                if (uiState is UiFlowState.Idle) {
                    uiState = UiFlowState.ShowingReward(event.premio)
                } else {
                    rewardQueue.add(event.premio)
                }
            }
        }
    }

    fun onLevelUpDismiss() {
        processNextEvent()
    }

    fun dismissReward() {
        processNextEvent()
    }

    private fun processNextEvent() {
        uiState = when {
            levelUpPendiente != null -> {
                val rango = levelUpPendiente!!
                levelUpPendiente = null
                UiFlowState.ShowingLevelUp(rango)
            }
            rewardQueue.isNotEmpty() -> {
                UiFlowState.ShowingReward(rewardQueue.removeAt(0))
            }
            else -> UiFlowState.Idle
        }
    }
}