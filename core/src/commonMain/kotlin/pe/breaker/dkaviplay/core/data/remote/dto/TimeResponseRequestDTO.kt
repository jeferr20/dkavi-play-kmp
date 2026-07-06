package pe.breaker.dkaviplay.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimeResponse(
    val unixtime: Long
)