package pe.breaker.dkaviplay.core.domain.model.inventory

object PremiosRegistry {
    val lista = listOf(
        // --- PREMIOS FÍSICOS ---
        DetallePremio(
            1,
            "TIZA PREDATOR",
            "Tiza de alta fricción para un control superior de la bola blanca.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_1"
        ),
        DetallePremio(
            2,
            "GUANTE PREMIUM",
            "Guante de licra profesional para un deslizamiento fluido del taco.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_2"
        ),
        DetallePremio(
            3,
            "PICADOR",
            "Herramienta para preparar el brillo y porosidad de la suela del taco.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_3"
        ),
        DetallePremio(
            4,
            "ESTUCHE DE BILLAR",
            "Maletín rígido protector para transportar tus tacos con seguridad.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_4"
        ),
        DetallePremio(
            5,
            "GRUESA DE TIZAS PREDATOR",
            "Caja completa de tizas profesionales para larga duración.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_5"
        ),
        DetallePremio(
            6,
            "EXTENSION DE BILLAR",
            "Accesorio acoplable para alcanzar tiros largos con comodidad.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_6"
        ),
        DetallePremio(
            7,
            "GUANTE PERSONALIZADO",
            "Guante profesional con diseño exclusivo de Dkavi Play.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_7"
        ),
        DetallePremio(
            8,
            "PORTA TIZA",
            "Accesorio magnético para tener tu tiza siempre al alcance.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_8"
        ),
        DetallePremio(
            9,
            "FIBRA DE CARBONO KONLEEN",
            "Vara de alta tecnología con mínima deflexión al tacar.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_9"
        ),
        DetallePremio(
            18,
            "DIPLOMA",
            "Certificado oficial de reconocimiento por méritos en el club.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_18"
        ),
        DetallePremio(
            19,
            "RETRATO DE SU NIVEL (AVATAR)",
            "Marco exclusivo para tu foto de perfil según tu rango actual.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_19"
        ),
        DetallePremio(
            20,
            "RECONOCIMIENTO FINAL",
            "Trofeo máximo otorgado al completar todos los desafíos de la temporada.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_20"
        ),
        DetallePremio(
            24,
            "TACO",
            "Taco de billar profesional para mejorar precisión y rendimiento en el juego.",
            TipoPremio.PREMIO_FISICO,
            MomentoUso.NINGUNO,
            imagenKey = "item_24"
        ),

        // --- ITEMS JUGABLES ---
        DetallePremio(
            10,
            "1 LANCE",
            "Te permite repetir un tiro fallido durante la partida.",
            TipoPremio.ITEM_JUGABLE,
            MomentoUso.ANTES_PARTIDA,
            imagenKey = "item_10"
        ),
        DetallePremio(
            11,
            "1 BOLA",
            "Otorga una bola extra a tu favor en el conteo final.",
            TipoPremio.ITEM_JUGABLE,
            MomentoUso.ANTES_PARTIDA,
            imagenKey = "item_11"
        ),
        DetallePremio(
            12,
            "1 GIRO RULETA",
            "Otorga un intento gratuito en la ruleta de premios diarios.",
            TipoPremio.ITEM_JUGABLE,
            MomentoUso.ANTES_PARTIDA,
            imagenKey = "item_12"
        ),
        DetallePremio(
            13,
            "OBELISCO (REVENTAR OBLIGADO)",
            "Obliga al oponente a realizar un tiro de apertura explosivo.",
            TipoPremio.ITEM_JUGABLE,
            MomentoUso.ANTES_PARTIDA,
            imagenKey = "item_13"
        ),
        DetallePremio(
            14,
            "ANULACION DE ATRIBUTO",
            "Cancela cualquier ventaja activa que tenga tu oponente.",
            TipoPremio.ITEM_JUGABLE,
            MomentoUso.ANTES_PARTIDA,
            imagenKey = "item_14"
        ),
        DetallePremio(
            15,
            "ANULACION DE ZONA POR UN TURNO",
            "Bloquea una tronera específica para tu rival durante un turno.",
            TipoPremio.ITEM_JUGABLE,
            MomentoUso.ANTES_PARTIDA,
            imagenKey = "item_15"
        ),
        DetallePremio(
            16,
            "RAYO (10s PARA TAQUEAR)",
            "Reduce el tiempo de tiro de tu oponente a solo 10 segundos.",
            TipoPremio.ITEM_JUGABLE,
            MomentoUso.ANTES_PARTIDA,
            imagenKey = "item_16"
        ),

        // --- MONETARIO ---
        DetallePremio(
            17,
            "CUPÓN DE S/.10",
            "Descuento aplicable en el pago de mesa o consumo local.",
            TipoPremio.MONETARIO,
            MomentoUso.DESCUENTO,
            imagenKey = "item_17"
        ),
        // --- RETOS ---
        DetallePremio(
            21,
            "RETO OBLIGADO",
            "Fuerza a un jugador de rango similar a aceptar tu desafío.",
            TipoPremio.RETO,
            MomentoUso.RETO,
            imagenKey = "item_21"
        ),
        DetallePremio(
            22,
            "RETO FUEGO GRATIS",
            "Participa en un reto de alta apuesta sin pagar comisión.",
            TipoPremio.RETO,
            MomentoUso.RETO,
            imagenKey = "item_22"
        ),
        DetallePremio(
            23,
            "RETO FINAL GRATIS",
            "Acceso gratuito al torneo de cierre de temporada.",
            TipoPremio.RETO,
            MomentoUso.RETO,
            imagenKey = "item_23"
        )
    )

    fun buscarPorId(id: Int): DetallePremio? = lista.find { it.id == id }
    fun obtenerByTipo(tipoItem: TipoPremio): List<DetallePremio> =
        lista.filter { it.tipo == tipoItem }
}