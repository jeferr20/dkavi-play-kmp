package pe.breaker.dkaviplay.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UbigeoItem(
    @SerialName("id_ubigeo") val id: String,
    @SerialName("nombre_ubigeo") val nombre: String,
    @SerialName("codigo_ubigeo") val codigo: String? = null
)