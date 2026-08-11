package pe.breaker.dkaviplay.presentation.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pe.breaker.dkaviplay.data.remote.dto.TimestampDTO

fun createTimestampDTO(dateStr: String, timeStr: String): TimestampDTO {
    val dateParts = dateStr.split("/")
    val timeParts = timeStr.split(":")

    val day = dateParts[0].toInt()
    val month = dateParts[1].toInt()
    val year = dateParts[2].toInt()
    val hour = timeParts[0].toInt()
    val minute = timeParts[1].toInt()

    val peruTimeZone = TimeZone.of("America/Lima")
    val localDateTime = LocalDateTime(year, month, day, hour, minute, 0, 0)
    val instant = localDateTime.toInstant(peruTimeZone)

    return TimestampDTO(
        seconds = instant.epochSeconds,
        nanoseconds = instant.nanosecondsOfSecond
    )
}

fun formatISOWithOffset(dateStr: String, timeStr: String): String {
    // dateStr = "24/07/2026", timeStr = "12:25"
    val dateParts = dateStr.split("/")
    val timeParts = timeStr.split(":")

    val day = dateParts[0].toInt()
    val month = dateParts[1].toInt()
    val year = dateParts[2].toInt()
    val hour = timeParts[0].toInt()
    val minute = timeParts[1].toInt()

    val timeZone = TimeZone.of("America/Lima")
    val localDateTime = LocalDateTime(year, month, day, hour, minute, 0, 0)

    // Obtenemos el instante exacto conservando el offset
    val instant = localDateTime.toInstant(timeZone)
    return instant.toString() // Produce: "2026-07-24T12:25:00-05:00" o "2026-07-24T17:25:00Z"
}