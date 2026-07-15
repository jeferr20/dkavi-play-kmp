package pe.breaker.dkaviplay.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LogMonedaDTO(
    val userUid: String,
    val cantidadMonedas: Int,
    val monto: Double,
    val sedeId: Int,
    val idempotencyKey: String
)