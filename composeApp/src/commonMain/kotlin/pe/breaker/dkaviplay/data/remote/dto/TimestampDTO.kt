package pe.breaker.dkaviplay.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimestampDTO(
    val seconds: Long,
    val nanoseconds: Int = 0
)