package pe.breaker.dkaviplay.presentation.mapper

import org.jetbrains.compose.resources.DrawableResource
import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio
import dkaviplay.composeapp.generated.resources.*

fun DetallePremio.toDrawableResource(): DrawableResource {
    return when (this.imagenKey) {
        "item_1" -> Res.drawable.item_1
        "item_2" -> Res.drawable.item_2
        "item_3" -> Res.drawable.item_3
        "item_4" -> Res.drawable.item_4
        "item_5" -> Res.drawable.item_5
        "item_6" -> Res.drawable.item_6
        "item_7" -> Res.drawable.item_7
        "item_8" -> Res.drawable.item_8
        "item_9" -> Res.drawable.item_9
        "item_10" -> Res.drawable.item_10
        "item_11" -> Res.drawable.item_11
        "item_12" -> Res.drawable.item_12
        "item_13" -> Res.drawable.item_13
        "item_14" -> Res.drawable.item_14
        "item_15" -> Res.drawable.item_15
        "item_16" -> Res.drawable.item_16
        "item_17" -> Res.drawable.item_17
        "item_18" -> Res.drawable.item_18
        "item_19" -> Res.drawable.item_19
        "item_20" -> Res.drawable.item_20
        "item_21" -> Res.drawable.item_21
        "item_22" -> Res.drawable.item_22
        "item_23" -> Res.drawable.item_23
        "item_24" -> Res.drawable.item_24
        else -> Res.drawable.item_1
    }
}