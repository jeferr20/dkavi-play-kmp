package pe.breaker.dkaviplay.core.domain.model

import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.core.domain.model.rango.Rango

sealed class GlobalEvent {
    data class ItemGained(val premio: DetallePremio) : GlobalEvent()
    data class LevelUp(val nuevoRango: Rango) : GlobalEvent()
}