package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable

@Serializable
data class ReservaFirebase(
    val fechaFin: Long? = null,
    val fechaInicio: Long? = null,
    val isMovil: Boolean?,
    val montoTotal: Double?,
    val montoTotalPagado: Double?,
    val reservaSimple: Boolean?,
    val tipoJuego: String?,
    val user1: String?,
    val user2: String?,
    val uuidEstado: Int?,
    val uuidMesa: String?,
    val uuidSede: String?,
    val uuidUser1: String?,
    val uuidUser2: String?,
    val uuidUserPendiente: String?,
    val esperandoConfirmacion: Boolean?,
    val user1Ready: Boolean?,
    val user2Ready: Boolean?,
)