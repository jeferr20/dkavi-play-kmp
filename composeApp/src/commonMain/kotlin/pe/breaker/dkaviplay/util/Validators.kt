package pe.breaker.dkaviplay.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.todayIn
import kotlin.time.Clock

fun String.isValidEmail() : Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$".toRegex()
    return this.isNotEmpty() && emailRegex.matches(this)
}

fun String.isValidPhone() : Boolean{
    val phoneRegex = "^9[0-9]{8}$".toRegex()
    return this.isNotEmpty() && phoneRegex.matches(this)
}

fun String.isMayorEdad(): Boolean {
    return try {
        val parts = this.split("/")
        if (parts.size != 3) return false

        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()

        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val birthDate = LocalDate(year, month, day)

        var age = today.year - birthDate.year

        if (today.month.number < birthDate.month.number ||
            (today.month.number == birthDate.month.number && today.day < birthDate.day)) {
            age--
        }

        age >= 15
    } catch (e: Exception) {
        false
    }
}