package pe.breaker.dkaviplay.presentation.util

import dkaviplay.composeapp.generated.resources.Res
import dkaviplay.composeapp.generated.resources.ic_no_item_available
import dkaviplay.composeapp.generated.resources.item_1
import dkaviplay.composeapp.generated.resources.item_10
import dkaviplay.composeapp.generated.resources.item_11
import dkaviplay.composeapp.generated.resources.item_12
import dkaviplay.composeapp.generated.resources.item_13
import dkaviplay.composeapp.generated.resources.item_14
import dkaviplay.composeapp.generated.resources.item_15
import dkaviplay.composeapp.generated.resources.item_16
import dkaviplay.composeapp.generated.resources.item_17
import dkaviplay.composeapp.generated.resources.item_18
import dkaviplay.composeapp.generated.resources.item_19
import dkaviplay.composeapp.generated.resources.item_2
import dkaviplay.composeapp.generated.resources.item_20
import dkaviplay.composeapp.generated.resources.item_21
import dkaviplay.composeapp.generated.resources.item_22
import dkaviplay.composeapp.generated.resources.item_23
import dkaviplay.composeapp.generated.resources.item_24
import dkaviplay.composeapp.generated.resources.item_3
import dkaviplay.composeapp.generated.resources.item_4
import dkaviplay.composeapp.generated.resources.item_5
import dkaviplay.composeapp.generated.resources.item_6
import dkaviplay.composeapp.generated.resources.item_7
import dkaviplay.composeapp.generated.resources.item_8
import dkaviplay.composeapp.generated.resources.item_9
import org.jetbrains.compose.resources.DrawableResource

object ItemResourceMapper {
    fun getItemImage(id: Int): DrawableResource {
        return when (id) {
            1 -> Res.drawable.item_1
            2 -> Res.drawable.item_2
            3 -> Res.drawable.item_3
            4 -> Res.drawable.item_4
            5 -> Res.drawable.item_5
            6 -> Res.drawable.item_6
            7 -> Res.drawable.item_7
            8 -> Res.drawable.item_8
            9 -> Res.drawable.item_9
            10 -> Res.drawable.item_10
            11 -> Res.drawable.item_11
            12 -> Res.drawable.item_12
            13 -> Res.drawable.item_13
            14 -> Res.drawable.item_14
            15 -> Res.drawable.item_15
            16 -> Res.drawable.item_16
            17 -> Res.drawable.item_17
            18 -> Res.drawable.item_18
            19 -> Res.drawable.item_19
            20 -> Res.drawable.item_20
            21 -> Res.drawable.item_21
            22 -> Res.drawable.item_22
            23 -> Res.drawable.item_23
            24 -> Res.drawable.item_24

            else -> Res.drawable.ic_no_item_available
        }
    }
}