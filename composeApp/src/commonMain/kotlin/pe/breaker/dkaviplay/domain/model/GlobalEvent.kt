package pe.breaker.dkaviplay.domain.model

import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.domain.model.rango.Rango

sealed class GlobalEvent {
    data class ItemGained(val premio: DetallePremio) : GlobalEvent()
    data class LevelUp(val nuevoRango: Rango) : GlobalEvent()
}