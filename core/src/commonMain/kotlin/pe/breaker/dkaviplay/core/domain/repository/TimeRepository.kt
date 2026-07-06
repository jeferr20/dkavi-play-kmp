package pe.breaker.dkaviplay.core.domain.repository

import kotlin.time.Instant

interface TimeRepository {
    suspend fun getServerTime(): Instant
}