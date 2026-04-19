package pe.breaker.dkaviplay.domain.repository

import kotlin.time.Instant

interface TimeRepository {
    suspend fun getServerTime(): Instant
}