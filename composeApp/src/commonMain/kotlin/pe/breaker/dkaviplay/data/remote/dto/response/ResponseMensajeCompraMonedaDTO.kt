package pe.breaker.dkaviplay.data.remote.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMensajeCompraMonedaDTO(
    val celular: String,
    val mensaje: String
)