package pe.breaker.dkaviplay.util

import kotlinx.datetime.DayOfWeek

enum class Dias(
    val nDia: Int,
    val dia: String,
    val day: String,
    val dayOfWeek: DayOfWeek
) {
    LUNES(
        nDia = 1,
        dia = "Lunes",
        day = "Monday",
        dayOfWeek = DayOfWeek.MONDAY
    ),
    MARTES(
        nDia = 2,
        dia = "Martes",
        day = "Tuesday",
        dayOfWeek = DayOfWeek.TUESDAY
    ),
    MIERCOLES(
        nDia = 3,
        dia = "Miércoles",
        day = "Wednesday",
        dayOfWeek = DayOfWeek.WEDNESDAY
    ),
    JUEVES(
        nDia = 4,
        dia = "Jueves",
        day = "Thursday",
        dayOfWeek = DayOfWeek.THURSDAY
    ),
    VIERNES(
        nDia = 5,
        dia = "Viernes",
        day = "Friday",
        dayOfWeek = DayOfWeek.FRIDAY
    ),
    SABADO(
        nDia = 6,
        dia = "Sábado",
        day = "Saturday",
        dayOfWeek = DayOfWeek.SATURDAY
    ),
    DOMINGO(
        nDia = 7,
        dia = "Domingo",
        day = "Sunday",
        dayOfWeek = DayOfWeek.SUNDAY
    );

    companion object {
        fun fromDayOfWeek(dayOfWeek: DayOfWeek): Dias? =
            entries.firstOrNull { it.dayOfWeek == dayOfWeek }

        fun fromNumero(numero: Int): Dias? =
            entries.firstOrNull { it.nDia == numero }

        fun fromNombre(nombre: String): Dias? =
            entries.firstOrNull {
                it.dia.equals(nombre, ignoreCase = true) || it.day.equals(nombre, ignoreCase = true)
            }
    }
}