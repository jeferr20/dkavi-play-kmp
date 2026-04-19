package pe.breaker.dkaviplay.data.util

import kotlin.math.pow
import kotlin.math.roundToInt

fun Double.format(digits: Int): String {
    val multiplier = 10.0.pow(digits)
    val rounded = (this * multiplier).roundToInt() / multiplier
    val parts = rounded.toString().split(".")

    val integerPart = parts[0]
    var decimalPart = if (parts.size > 1) parts[1] else ""

    // Rellenamos con ceros si falta precisión (ej: 25.5 -> 25.50)
    while (decimalPart.length < digits) {
        decimalPart += "0"
    }

    return if (digits > 0) "$integerPart.$decimalPart" else integerPart
}