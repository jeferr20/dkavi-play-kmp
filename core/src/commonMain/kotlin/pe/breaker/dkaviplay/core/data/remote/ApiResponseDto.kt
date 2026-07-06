package pe.breaker.dkaviplay.core.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    val ok: Boolean,
    val data: T? = null,
    val message: String? = null
)