package pe.breaker.dkaviplay.core.domain.model.inventory

import org.jetbrains.compose.resources.DrawableResource

enum class TipoPremio {
    PREMIO_FISICO,
    ITEM_JUGABLE,
    MONETARIO,
    RETO
}

enum class MomentoUso {
    ANTES_PARTIDA,
    NINGUNO,
    DESCUENTO,
    RETO,
}

data class DetallePremio(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val tipo: TipoPremio,
    val uso: MomentoUso,
    val cantidad: Int = 0,
    val imagenKey: String
)