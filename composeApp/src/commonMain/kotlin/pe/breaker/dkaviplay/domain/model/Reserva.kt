package pe.breaker.dkaviplay.domain.model

import pe.breaker.dkaviplay.data.remote.dto.ResultadosPartidasDTO

data class Reserva(
    val reservaUid: String,
    val estado: String,
    val estadoColor: String,
    val estadoInt: Int,
    val sedeImagen: String,
    val sede: String,
    val sedeUid: String,
    val tipoJuego: String,
    val fechaInicio: String,
    val fechaFin: String,
    val montoTotal: Double,
    val montoPagado: Double,
    val mesa: String,
    val creador: String,
    val creadorUid: String,
    val retado: String,
    val retadoUid: String,
    val userPendienteUid: String,
    val juegoUid: String,
    val esperandoConfirmacion: Boolean,
    val partidas: List<ResultadosPartidasDTO>,
    val ganadorUid: String,
    val userCreadorReady: Boolean,
    val userRetadoReady: Boolean,
)