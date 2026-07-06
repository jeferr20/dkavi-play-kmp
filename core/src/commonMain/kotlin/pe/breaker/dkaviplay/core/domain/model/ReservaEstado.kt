package pe.breaker.dkaviplay.core.domain.model

enum class ReservaEstado(
    val id: Int,
    val descripcion: String,
    val colorHex: String
) {
    PENDIENTE_RESPUESTA(1, "Pendiente de Respuesta", "#6c757d"),
    RETO_RECHAZADO(2, "Reto Rechazado", "#dc3545"),
    RESERVA_PENDIENTE(3, "Reserva Pendiente", "#ffc107"),
    RESERVA_RECHAZADO(4, "Rechazado", "#dc3545"),
    APROBADO(5, "Aprobado", "#28a745"),
    EN_JUEGO(6, "En juego", "#007bff"),
    FINALIZADO(7, "Finalizado", "#28a745");

    fun puedeCancelarse(): Boolean = this == PENDIENTE_RESPUESTA
    fun puedeGestionarReto(): Boolean = this == PENDIENTE_RESPUESTA
    fun puedeVerJuego(): Boolean = this == APROBADO || this == EN_JUEGO
    fun mostrarSaldo() : Boolean = this == APROBADO
    fun puedeDarResultado () : Boolean = this == EN_JUEGO
    fun puedeVerResultado() : Boolean = this == FINALIZADO
    fun puedeEscogerItem() : Boolean = this == APROBADO
    fun puedePagarse() : Boolean = this == RESERVA_PENDIENTE

    companion object {
        fun fromId(id: Int?): ReservaEstado {
            return entries.find { it.id == id } ?: PENDIENTE_RESPUESTA
        }
    }
}