package pe.breaker.dkaviplay.core.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import pe.breaker.dkaviplay.core.data.remote.dto.TimestampDTO
import kotlin.time.Clock
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

private fun timestampToString(seconds: Long): String {
    val instant = Instant.fromEpochSeconds(seconds)
    val dateTime = instant.toLocalDateTime(TimeZone.of("America/Lima"))

    val day = dateTime.day.toString().padStart(2, '0')
    val month = dateTime.month.number.toString().padStart(2, '0')
    val year = dateTime.year

    return "$day/$month/$year"
}

fun obtenerTiempoRestante(fechaInicioStr: String): String {
    try {
        val partes = fechaInicioStr.split(" ")
        val fechaPartes = partes[0].split("/")
        val horaPartes = partes[1].split(":")

        val fechaTorneo = LocalDateTime(
            fechaPartes[2].toInt(),
            fechaPartes[1].toInt(),
            fechaPartes[0].toInt(),
            horaPartes[0].toInt(),
            horaPartes[1].toInt(), 0, 0
        ).toInstant(TimeZone.of("America/Lima"))

        val ahora = Clock.System.now()
        val totalDiff = fechaTorneo - ahora

        if (totalDiff.isNegative()) return "Ya empezó"

        val dias = totalDiff.inWholeDays
        val horas = totalDiff.inWholeHours % 24
        val minutos = totalDiff.inWholeMinutes % 60

        return buildString {
            append("Empieza en ")
            if (dias > 0) append("${dias}d ")
            if (horas > 0) append("${horas}h ")
            if (minutos > 0 || (dias == 0L && horas == 0L)) append("${minutos}m")
        }.trim()
    } catch (e: Exception) {
        println("Error al analizar la fecha: ${e.message}")
        return "Fecha pendiente"
    }
}

fun formatMillisToDate(millis: Long): String {
    val localDate = Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.UTC).date

    val day = localDate.day.toString().padStart(2, '0')
    val month = localDate.month.number.toString().padStart(2, '0')
    val year = localDate.year

    return "$day/$month/$year"
}

fun parseStringToLocalDateTime(dateString: String): LocalDateTime {
    // Formato esperado: "dd/MM/yyyy HH:mm"
    val parts = dateString.split(" ")
    val dateParts = parts[0].split("/")
    val timeParts = parts[1].split(":")

    return LocalDateTime(
        year = dateParts[2].toInt(),
        month = dateParts[1].toInt(),
        day = dateParts[0].toInt(),
        hour = timeParts[0].toInt(),
        minute = timeParts[1].toInt()
    )
}

fun obtenerNombreDiaActual(): String {
    val currentMoment: Instant = Clock.System.now()
    val localDateTime: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())

    // 2. Obtener el día de la semana
    return when (localDateTime.dayOfWeek) {
        DayOfWeek.MONDAY -> "Lunes"
        DayOfWeek.TUESDAY -> "Martes"
        DayOfWeek.WEDNESDAY -> "Miércoles"
        DayOfWeek.THURSDAY -> "Jueves"
        DayOfWeek.FRIDAY -> "Viernes"
        DayOfWeek.SATURDAY -> "Sábado"
        DayOfWeek.SUNDAY -> "Domingo"
    }
}

fun formatToRelativeDate(fechaString: String): String {
    val localDateTime = parseStringToLocalDateTime(fechaString)
    val fecha = localDateTime.date
    val hoy = Clock.System.now().toLocalDateTime(TimeZone.of("America/Lima")).date

    return when (fecha) {
        hoy -> "HOY"
        hoy.plus(1, DateTimeUnit.DAY) -> "MAÑANA"
        hoy.minus(1, DateTimeUnit.DAY) -> "AYER"
        else -> {
            val mes = when (fecha.month.number) {
                1 -> "ENE"
                2 -> "FEB"
                3 -> "MAR"
                4 -> "ABR"
                5 -> "MAY"
                6 -> "JUN"
                7 -> "JUL"
                8 -> "AGO"
                9 -> "SET"
                10 -> "OCT"
                11 -> "NOV"
                12 -> "DIC"
                else -> ""
            }
            "${fecha.day} $mes"
        }
    }
}