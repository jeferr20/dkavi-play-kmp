package pe.breaker.dkaviplay.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TimestampResponseDTO(
    val _seconds: Long,
    val _nanoseconds: Int = 0
)