package pe.breaker.dkaviplay.presentation.util

fun filterInputByType(
    type: InputType,
    value: String
): String {
    return when (type) {
        InputType.NOMBRE -> {
            value
                .replace(Regex("[^a-zA-ZáéíóúÁÉÍÓÚñÑ ]"), "")
                .split(" ")
                .joinToString(" ") { word ->
                    word.lowercase().replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                }
        }

        InputType.CORREO -> {
            value.lowercase()
        }

        InputType.CELULAR -> {
            value.filter { it.isDigit() }.take(9)
        }

        InputType.TEXTO -> {
            value
                .lowercase()
                .replaceFirstChar { char ->
                    if (char.isLowerCase()) char.titlecase() else char.toString()
                }
        }
        InputType.PASSWORD -> value
        InputType.NUMERO -> {
            value.filter { it.isDigit() }
        }
    }
}