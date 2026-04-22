package pe.breaker.dkaviplay.domain.model

enum class TipoJuego(
    val nombre: String,
    val descripcion: String,
    val setsMaximos: Int
) {
    BILLAR(
        nombre = "BILLAR",
        descripcion = "Partida única. Gana quien llegue al puntaje acordado.",
        setsMaximos = 1
    ),
    POOL(
        nombre = "POOL",
        descripcion = "Duelo al mejor de 3 sets (Gana 2 de 3).",
        setsMaximos = 3
    );

    companion object {
        fun fromString(value: String?): TipoJuego {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: POOL
        }
    }

    fun esVictoria(victorias: Int): Boolean {
        return when (this) {
            BILLAR -> victorias >= 1
            POOL -> victorias >= 2
        }
    }
}