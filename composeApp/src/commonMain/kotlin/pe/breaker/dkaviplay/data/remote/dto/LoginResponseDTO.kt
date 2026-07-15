package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    val token: String? = null,
    val usuarioCompleto: Boolean? = true,
    val usuarioId: Int? = null,
    val firebaseToken: String? = null,
    val supabaseToken: String? = null,
    val cripKey: String? = null
)