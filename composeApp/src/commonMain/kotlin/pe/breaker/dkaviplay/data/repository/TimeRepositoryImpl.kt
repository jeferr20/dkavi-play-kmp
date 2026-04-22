package pe.breaker.dkaviplay.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import pe.breaker.dkaviplay.data.remote.dto.TimeResponse
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.domain.repository.TimeRepository
import kotlin.time.Clock
import kotlin.time.Instant

class TimeRepositoryImpl(
    private val httpClient: HttpClient,
) : TimeRepository {
    override suspend fun getServerTime(): Instant {
        return try {
            val response: TimeResponse = httpClient.get(ConstatesCloud.TIMEAPI).body()
            Instant.fromEpochMilliseconds(response.unixtime * 1000)
        } catch (e: Exception) {
            Clock.System.now()
        }
    }
}