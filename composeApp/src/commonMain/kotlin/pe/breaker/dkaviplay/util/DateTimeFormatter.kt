package pe.breaker.dkaviplay.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

interface DateTimeFormatter {
    fun parseIsoToInstant(dateString: String?): Instant?
    fun parseIsoToLocalDateTime(dateString: String?): LocalDateTime?
    fun formatTimeToHHMM(dateString: String?): String
    fun formatToRelativeDate(dateString: String?, now: Instant): String
    fun formatToFullDateTime(dateString: String?): String
    fun formatMillisToDate(millis: Long): String
    fun parseLocalDateTime(dateStr: String, timeStr: String): LocalDateTime
    fun parseToInstant(dateStr: String, timeStr: String): Instant
    fun toIsoStringWithOffset(dateStr: String, timeStr: String): String
}

class DateTimeFormatterImpl : DateTimeFormatter {
    private val timeZone = TimeZone.of("America/Lima")
    private val mesNombres = arrayOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SET", "OCT", "NOV", "DIC")

    override fun parseIsoToInstant(dateString: String?): Instant? {
        if (dateString.isNullOrBlank()) return null
        return runCatching { Instant.parse(dateString) }.getOrNull()
    }

    override fun parseIsoToLocalDateTime(dateString: String?): LocalDateTime? {
        return parseIsoToInstant(dateString)?.toLocalDateTime(timeZone)
    }

    override fun formatTimeToHHMM(dateString: String?): String {
        val ldt = parseIsoToLocalDateTime(dateString) ?: return ""
        return "${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"
    }

    override fun formatToRelativeDate(dateString: String?, now: Instant): String {
        val ldt = parseIsoToLocalDateTime(dateString) ?: return ""
        val fechaReserva = ldt.date
        val hoy = now.toLocalDateTime(timeZone).date

        return when (fechaReserva) {
            hoy -> "HOY"
            hoy.plus(1, DateTimeUnit.DAY) -> "MAÑANA"
            hoy.minus(1, DateTimeUnit.DAY) -> "AYER"
            else -> {
                val mes = mesNombres.getOrNull(fechaReserva.month.number - 1).orEmpty()
                "${fechaReserva.day} $mes"
            }
        }
    }

    override fun formatToFullDateTime(dateString: String?): String {
        val ldt = parseIsoToLocalDateTime(dateString) ?: return ""
        val day = ldt.day.toString().padStart(2, '0')
        val month = ldt.month.number.toString().padStart(2, '0')
        val hour = ldt.hour.toString().padStart(2, '0')
        val minute = ldt.minute.toString().padStart(2, '0')

        return "$day/$month/${ldt.year} $hour:$minute"
    }

    override fun formatMillisToDate(millis: Long): String {
        val localDate = kotlin.time.Instant.fromEpochMilliseconds(millis)
            .toLocalDateTime(TimeZone.UTC).date

        val day = localDate.day.toString().padStart(2, '0')
        val month = localDate.month.number.toString().padStart(2, '0')
        val year = localDate.year

        return "$day/$month/$year"
    }

    override fun parseLocalDateTime(dateStr: String, timeStr: String): LocalDateTime {
        val dateParts = dateStr.split("/")
        val timeParts = timeStr.split(":")

        return LocalDateTime(
            year = dateParts[2].toInt(),
            month = dateParts[1].toInt(),
            day = dateParts[0].toInt(),
            hour = timeParts[0].toInt(),
            minute = timeParts[1].toInt(),
            second = 0,
            nanosecond = 0
        )
    }

    override fun parseToInstant(dateStr: String, timeStr: String): Instant {
        val localDateTime = parseLocalDateTime(dateStr, timeStr)
        return localDateTime.toInstant(timeZone)
    }

    override fun toIsoStringWithOffset(dateStr: String, timeStr: String): String {
        return parseToInstant(dateStr, timeStr).toString()
    }
}