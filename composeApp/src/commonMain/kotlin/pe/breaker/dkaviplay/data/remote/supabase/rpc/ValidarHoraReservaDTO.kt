package pe.breaker.dkaviplay.data.remote.supabase.rpc

import kotlinx.serialization.Serializable

@Serializable
data class ValidarHoraReservaDTO(
    val valido: Boolean,
    val codigo: String? = null,
    val mensaje: String
)