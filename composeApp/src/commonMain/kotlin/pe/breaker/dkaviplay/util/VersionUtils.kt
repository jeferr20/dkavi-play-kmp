package pe.breaker.dkaviplay.util

fun isVersionOlder(current: String, required: String): Boolean {
    val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }
    val requiredParts = required.split(".").mapNotNull { it.toIntOrNull() }

    val maxLength = maxOf(currentParts.size, requiredParts.size)

    for (i in 0 until maxLength) {
        val curr = currentParts.getOrElse(i) { 0 }
        val req = requiredParts.getOrElse(i) { 0 }
        if (curr < req) return true
        if (curr > req) return false
    }
    return false
}