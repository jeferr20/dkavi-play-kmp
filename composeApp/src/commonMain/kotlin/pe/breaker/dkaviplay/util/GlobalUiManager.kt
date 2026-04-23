package pe.breaker.dkaviplay.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.GlobalEvent
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.domain.model.rango.Rango

class GlobalUiManager(
    private val sessionManager: UserSessionManager
) : ScreenModel {

    // Estados que observa el AppStateHandler
    var premioGanadoGeneral by mutableStateOf<DetallePremio?>(null)
    var levelUpData by mutableStateOf<Rango?>(null)
    var esperandoPremioPorSubida by mutableStateOf(false)

    // Variable interna para guardar el premio si llega mientras el LevelUp está en pantalla
    private var premioPendientePorRango: DetallePremio? = null

    init {
        // Escucha global desde que nace el Singleton
        screenModelScope.launch {
            sessionManager.globalEvent.collect { event ->
                procesarEvento(event)
            }
        }
    }

    private fun procesarEvento(event: GlobalEvent) {
        when (event) {
            is GlobalEvent.ItemGained -> {
                when {
                    // Caso 1: Estábamos esperando un premio específicamente por un Level Up
                    esperandoPremioPorSubida -> {
                        premioGanadoGeneral = event.premio
                        esperandoPremioPorSubida = false
                    }
                    // Caso 2: El usuario subió de nivel pero aún no cierra el diálogo
                    levelUpData != null -> {
                        premioPendientePorRango = event.premio
                    }
                    // Caso 3: Ganó un item normal (por ejemplo, compra o regalo directo)
                    else -> {
                        premioGanadoGeneral = event.premio
                    }
                }
            }

            is GlobalEvent.LevelUp -> {
                levelUpData = event.nuevoRango
            }
        }
    }

    fun handleLevelUpDismiss() {
        val rangoActual = levelUpData ?: return
        levelUpData = null // Cerramos el popup de nivel

        // Si el rango tenía recompensas, verificamos si ya llegaron o hay que esperar
        if (rangoActual.recompensas.isNotEmpty()) {
            if (premioPendientePorRango != null) {
                // El premio llegó mientras el usuario leía el LevelUp, lo soltamos ahora
                premioGanadoGeneral = premioPendientePorRango
                premioPendientePorRango = null
            } else {
                // El premio aún no llega por el Stream, activamos el loading
                esperandoPremioPorSubida = true
            }
        }
    }

    fun dismissPremio() {
        premioGanadoGeneral = null
    }

    fun timeoutEsperandoPremio() {
        esperandoPremioPorSubida = false
    }
}