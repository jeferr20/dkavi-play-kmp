package pe.breaker.dkaviplay.data.remote.supabase.view

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TarifarioView(
    @SerialName("sede_id")
    val sedeId: Int,

    @SerialName("monedas_reto")
    val cantidadMonedasReto: Double,

    @SerialName("precio_monedas")
    val precioMonedas: Double,

    @SerialName("numero_pago")
    val numeroPago: String
)