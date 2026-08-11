package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDTO(
    val token: String? = null,
    val usuarioCompleto: Boolean? = true,
    val firebaseToken: String? = null,
    val supabaseToken: String? = null,
    val refreshToken: String? = null,
    val cripKey: String? = null
)