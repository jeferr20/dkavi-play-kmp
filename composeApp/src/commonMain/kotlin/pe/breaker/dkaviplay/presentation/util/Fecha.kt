package pe.breaker.dkaviplay.presentation.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import pe.breaker.dkaviplay.data.remote.dto.TimestampDTO
import kotlin.time.Instant

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

fun timestampToStringCompleto(seconds: Long): String {
    val instant = Instant.fromEpochSeconds(seconds)
    val dateTime = instant.toLocalDateTime(TimeZone.of("America/Lima"))

    val day = dateTime.day.toString().padStart(2, '0')
    val month = dateTime.month.number.toString().padStart(2, '0')
    val year = dateTime.year

    // Extraemos hora y minutos
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')

    return "$day/$month/$year $hour:$minute"
}
fun formatMillisToDate(millis: Long): String {
    val localDate = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.UTC).date

    val day = localDate.day.toString().padStart(2, '0')
    val month = localDate.month.number.toString().padStart(2, '0')
    val year = localDate.year

    return "$day/$month/$year"
}
