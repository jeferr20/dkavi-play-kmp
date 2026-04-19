package pe.breaker.dkaviplay.data.remote.firebase

import kotlinx.serialization.Serializable
import pe.breaker.dkaviplay.data.remote.dto.ResultadosPartidasDTO

@Serializable
data class JuegoFirebase (
    val uuidJuego:String?,
    val arbitro: String?,
    val confirmacionJugador1: Boolean?,
    val confirmacionJugador2: Boolean?,
    val estado: String?,
    val partidas: List<ResultadosPartidasDTO>?,
    val reservaId: String?,
    val tipoJuego: String?,
    val tipoValidacion: String?,
    val user1: String?,
    val user2: String?,
    val uuidArbitro: String?,
    val uuidGanador: String?,
    val uuidUser1: String?,
    val uuidUser2: String?
)