package pe.breaker.dkaviplay.data.mapper

import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.data.remote.dto.ResultadosPartidasDTO
import pe.breaker.dkaviplay.data.remote.supabase.rpc.ReservaJuegoDTO
import pe.breaker.dkaviplay.data.remote.supabase.rpc.ReservaRpcDTO
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.ReservaEstado

object ReservaMapper {
    private val jsonMapper = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun mapToDomain(
        dto: ReservaRpcDTO
    ) : Reserva {
        return Reserva(
            reservaUid = dto.reservaId.toString(),
            estado = dto.estado,
            estadoColor = dto.estadoColor,
            estadoInt = dto.estadoId,
            sedeImagen = dto.sedeImagen ?: "",
            sede = dto.sede,
            sedeUid = dto.sedeId.toString(),
            tipoJuego = dto.tipoJuego,
            fechaInicio = dto.fechaInicio,
            fechaFin = dto.fechaFin,
            montoTotal = dto.montoTotal,
            mesa = dto.mesa,
            creador = dto.creador,
            creadorUid = dto.creadorId,
            retado = dto.retado,
            retadoUid = dto.retadoId,
            userPendienteUid = dto.userPendienteId ?: "",
            juegoUid = "",
            esperandoConfirmacion = dto.esperandoConfirmacion,
            partidas = emptyList(),
            ganadorUid = "",
            userCreadorReady = dto.creadorReady,
            userRetadoReady = dto.retadoReady
        )
    }

    fun mapJuegoToDomain(
        dto: ReservaJuegoDTO
    ) : Reserva {
        val estadoEnum = ReservaEstado.fromId(dto.estadoId)
        val listaPartidas: List<ResultadosPartidasDTO> = if (!dto.partidas.isNullOrBlank()) {
            try {
                jsonMapper.decodeFromString<List<ResultadosPartidasDTO>>(dto.partidas)
            } catch (e: Exception) {
                println("⚠️ Error deserializando partidas JSON String: ${e.message}")
                emptyList()
            }
        } else {
            emptyList()
        }
        return Reserva(
            reservaUid = dto.reservaId.toString(),
            estado = estadoEnum.descripcion,
            estadoColor = estadoEnum.colorHex,
            estadoInt = estadoEnum.id,
            sedeImagen = dto.sedeLogo,
            sede = dto.sede,
            sedeUid = dto.sedeId.toString(),
            tipoJuego = dto.tipoJuego,
            fechaInicio = dto.fechaInicio,
            fechaFin = dto.fechaFin,
            montoTotal = dto.montoTotal,
            mesa = dto.mesa,
            creador = dto.creador,
            creadorUid = dto.creadorUid,
            retado = dto.retado,
            retadoUid = dto.retadoUid,
            userPendienteUid = dto.userPendienteUid,
            juegoUid = dto.juegoId.toString(),
            esperandoConfirmacion = dto.esperandoConfirmacion,
            partidas = listaPartidas,
            ganadorUid = dto.ganadorUid ?: "",
            userCreadorReady = dto.userCreadorReady,
            userRetadoReady = dto.userRetadoReady
        )
    }
}