package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimestampDTO(
    val seconds: Long,
    val nanoseconds: Int = 0
)