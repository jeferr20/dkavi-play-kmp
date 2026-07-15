package pe.breaker.dkaviplay.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

interface DateTimeFormatter {
    fun parseIsoToInstant(dateString: String?): Instant?
    fun parseIsoToLocalDateTime(dateString: String?): LocalDateTime?
    fun formatTimeToHHMM(dateString: String?): String
    fun formatToRelativeDate(dateString: String?, now: Instant): String
    fun formatToFullDateTime(dateString: String?): String
}
class DateTimeFormatterImpl : DateTimeFormatter {
    private val timeZone = TimeZone.of("America/Lima")

    override fun parseIsoToInstant(dateString: String?): Instant? {
        if (dateString.isNullOrBlank()) return null
        return try {
            Instant.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }

    override fun parseIsoToLocalDateTime(dateString: String?): LocalDateTime? {
        return parseIsoToInstant(dateString)?.toLocalDateTime(timeZone)
    }

    override fun formatTimeToHHMM(dateString: String?): String {
        val localDateTime = parseIsoToLocalDateTime(dateString) ?: return ""
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }

    override fun formatToRelativeDate(dateString: String?, now: Instant): String {
        val localDateTime = parseIsoToLocalDateTime(dateString) ?: return ""
        val fechaReserva = localDateTime.date
        val hoy = now.toLocalDateTime(timeZone).date

        return when (fechaReserva) {
            hoy -> "HOY"
            hoy.plus(1, DateTimeUnit.DAY) -> "MAÑANA"
            hoy.minus(1, DateTimeUnit.DAY) -> "AYER"
            else -> {
                val mes = when (fechaReserva.month.number) {
                    1 -> "ENE"; 2 -> "FEB"; 3 -> "MAR"; 4 -> "ABR"; 5 -> "MAY"; 6 -> "JUN"
                    7 -> "JUL"; 8 -> "AGO"; 9 -> "SET"; 10 -> "OCT"; 11 -> "NOV"; 12 -> "DIC"
                    else -> ""
                }
                "${fechaReserva.dayOfMonth} $mes"
            }
        }
    }

    override fun formatToFullDateTime(dateString: String?): String {
        val localDateTime = parseIsoToLocalDateTime(dateString) ?: return ""

        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val month = localDateTime.month.number.toString().padStart(2, '0')
        val year = localDateTime.year
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')

        return "$day/$month/$year $hour:$minute"
    }
}