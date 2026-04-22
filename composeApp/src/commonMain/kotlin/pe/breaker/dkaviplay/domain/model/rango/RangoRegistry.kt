package pe.breaker.dkaviplay.domain.model.rango

import pe.breaker.dkaviplay.domain.model.inventory.PremiosRegistry

object RangoRegistry {
     val niveles = listOf(
         RangoRaw(1, "Novato", 0, 1000, listOf(13)),
         RangoRaw(2, "Recluta", 1001, 1300, listOf(1, 13)),
         RangoRaw(3, "Aprendiz", 1301, 1700, listOf(10)),
         RangoRaw(4, "Jinete", 1701, 2300, listOf(11)),
         RangoRaw(5, "Constructor", 2301, 3000, listOf(2)),
         RangoRaw(6, "Centinela", 3001, 3800, listOf(12)),
         RangoRaw(7, "Roca", 3801, 4600, listOf(18)),
         RangoRaw(8, "Guerrero", 4601, 5600, listOf(21)),
         RangoRaw(9, "Samurái", 5601, 6700, listOf(19)),
         RangoRaw(10, "Asesino", 6701, 8000, listOf(17, 17)),
         RangoRaw(11, "Bronce", 8001, 9600, listOf(24)),
         RangoRaw(12, "Plata", 9601, 11300, listOf(22)),
         RangoRaw(13, "Oro", 11301, 13200, listOf(1)),
         RangoRaw(14, "Platino", 13201, 15200, listOf(14)),
         RangoRaw(15, "Diamante", 15201, 17500, listOf(3)),
         RangoRaw(16, "Kriptón", 17501, 19900, listOf(15)),
         RangoRaw(17, "Maestro", 19901, 22400, listOf(4)),
         RangoRaw(18, "Gran Maestro", 22401, 25000, listOf(13)),
         RangoRaw(19, "Destructor", 25001, 27800, listOf(5)),
         RangoRaw(20, "Devastador", 27801, 30700, listOf(16, 19)),
         RangoRaw(21, "Conquistador", 30701, 33700, listOf(13)),
         RangoRaw(22, "Emperador", 33701, 37200, listOf(6, 23)),
         RangoRaw(23, "Arcángel", 37201, 41200, listOf(7, 21)),
         RangoRaw(24, "Infernal", 41201, 45200, listOf(8, 12)),
         RangoRaw(25, "Inmortal", 45201, null, listOf(9, 20))
    )

    fun obtenerRangoPorPuntos(puntos: Int): Rango {
        val raw = niveles.find {
            puntos >= it.puntosMin && (it.puntosMax == null || puntos <= it.puntosMax)
        } ?: niveles.first()

        return Rango(
            nivel = raw.nivel,
            categoria = raw.categoria,
            puntosMin = raw.puntosMin,
            puntosMax = raw.puntosMax,
            recompensas = raw.recompensasIds.mapNotNull { id ->
                PremiosRegistry.buscarPorId(id)
            }
        )
    }

    fun obtenerSiguienteRango(nivelActual: Int): RangoRaw? {
        return niveles.find { it.nivel == nivelActual + 1 }
    }

    fun obtenerRangoAnterior(nivelActual: Int): RangoRaw?{
        return niveles.find { it.nivel == nivelActual -1 }
    }

    fun obtenerTodosLosRangos(): List<Rango> {
        return niveles.map { raw ->
            Rango(
                nivel = raw.nivel,
                categoria = raw.categoria,
                puntosMin = raw.puntosMin,
                puntosMax = raw.puntosMax,
                recompensas = raw.recompensasIds.mapNotNull { id ->
                    PremiosRegistry.buscarPorId(id)
                }
            )
        }
    }
}