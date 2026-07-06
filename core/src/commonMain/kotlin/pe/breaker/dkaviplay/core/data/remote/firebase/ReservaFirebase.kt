package pe.breaker.dkaviplay.core.data.remote.firebase

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class ReservaFirebase(
    val fechaFin: Timestamp? = null,
    val fechaInicio: Timestamp? = null,
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