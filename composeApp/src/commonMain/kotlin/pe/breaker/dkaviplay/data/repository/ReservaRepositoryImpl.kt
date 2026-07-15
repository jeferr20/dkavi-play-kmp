package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import pe.breaker.dkaviplay.data.mapper.mapToReserva
import pe.breaker.dkaviplay.data.remote.dto.AceptarRetoRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.data.remote.firebase.JuegoFirebase
import pe.breaker.dkaviplay.data.remote.firebase.MesaFirebase
import pe.breaker.dkaviplay.data.remote.firebase.ReservaFirebase
import pe.breaker.dkaviplay.data.remote.firebase.SedeFirebase
import pe.breaker.dkaviplay.data.remote.supabase.ReservaDTO
import pe.breaker.dkaviplay.data.remote.supabase.view.MesaSedeView
import pe.breaker.dkaviplay.data.remote.supabase.view.UserMovilView
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.ReservaEstado
import pe.breaker.dkaviplay.domain.repository.ReservaRepository
import pe.breaker.dkaviplay.domain.repository.TimeRepository

class ReservaRepositoryImpl(
    private val httpClient: HttpClient,
    private val firestore: FirebaseFirestore,
    private val timeRepository: TimeRepository,
    private val supabaseClient: SupabaseClient
) : ReservaRepository {

    override suspend fun registroReserva(reserva: RegisterReservaRequestDTO): Result<String> {
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/reserva") {
            contentType(ContentType.Application.Json)
            setBody(reserva)
        }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun getReservaById(reservaId: String): Result<Reserva> {
        return try {
            val snapshot = firestore.collection("Reserva").document(reservaId).get()

            if (!snapshot.exists) {
                return Result.failure(Exception("La reserva no existe o ya no se encuentra disponible"))
            }

            val reservaFirebase = snapshot.data<ReservaFirebase>()

            val (sede, mesa, juego) = coroutineScope {
                val sedeDef = async {
                    reservaFirebase.uuidSede?.let { id ->
                        try {
                            firestore.collection("Sede").document(id).get().data<SedeFirebase>()
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                val mesaDef = async {
                    reservaFirebase.uuidMesa?.let { id ->
                        try {
                            firestore.collection("Mesa").document(id).get().data<MesaFirebase>()
                        } catch (e: Exception) {
                            null
                        }
                    }
                }

                val juegoDef = async {
                    try {
                        val juegoSnapshot = firestore.collection("Juego")
                            .where { "reservaId" equalTo reservaId }
                            .limit(1)
                            .get()

                        val doc = juegoSnapshot.documents.firstOrNull()

                        doc?.data<JuegoFirebase>()?.copy(uuidJuego = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                Triple(sedeDef.await(), mesaDef.await(), juegoDef.await())
            }

            if (sede == null || mesa == null) {
                return Result.failure(Exception("No se pudo cargar la información de la sede"))
            }

            val reservaDominio = mapToReserva(
                id = reservaId,
                sede = sede,
                reserva = reservaFirebase,
                mesa = mesa,
                juego = juego
            )

            Result.success(reservaDominio)

        } catch (e: Exception) {
            println("Error getReservaById en Pool Street: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(SupabaseExperimental::class)
    override fun getReservasFlow(usuarioUid: String?): Flow<List<Reserva>> {
        val userUid = usuarioUid ?: return flowOf(emptyList())

        val flowJugador1 = supabaseClient.from(schema = "dkavi", table = "Reserva")
            .selectAsFlow(primaryKey = ReservaDTO::id, channelName = "dkavi.Reserva.u1:$userUid",
                filter = FilterOperation("uuid_user1", FilterOperator.EQ, userUid))

        val flowJugador2 = supabaseClient.from(schema = "dkavi", table = "Reserva")
            .selectAsFlow(primaryKey = ReservaDTO::id, channelName = "dkavi.Reserva.u2:$userUid",
                filter = FilterOperation("uuid_user2", FilterOperator.EQ, userUid))

        val flowReservasUnicas = combine(flowJugador1, flowJugador2) { lista1, lista2 ->
            (lista1 + lista2).distinctBy { it.id }.filter { it.status }
        }

        val flowMesasSedes = flow {
            val mapaVista = supabaseClient.from(schema = "public", table = "v_MesasSedes")
                .select().decodeList<MesaSedeView>().associateBy { it.mesaId }
            emit(mapaVista)
        }

        val flowUsuariosInvolucrados = flowReservasUnicas.map { reservas ->
            val userUids = reservas.flatMap { listOfNotNull(it.uuidUser1, it.uuidUser2) }.distinct()

            if (userUids.isEmpty()) return@map emptyMap()

            try {
                supabaseClient.from(schema = "seguridad", table = "v_UsuariosMovilesInfo")
                    .select {
                        filter {
                            isIn("user_uuid_auth", userUids)
                        }
                    }
                    .decodeList<UserMovilView>()
                    .associateBy { it.userUid }
            } catch (e: Exception) {
                println("❌ Error trayendo usuarios: ${e.message}")
                emptyMap()
            }
        }

        return combine(flowReservasUnicas, flowMesasSedes, flowUsuariosInvolucrados) { reservas, mapaVista, mapaUsuarios ->
            reservas.mapNotNull { dto ->
                val infoMaestra = mapaVista[dto.idMesa?.toInt()]

                if (infoMaestra != null) {
                    val estadoEnum = ReservaEstado.fromId(dto.idEstado?.toInt())

                    // 💥 Buscamos los nombres de los jugadores en nuestra caché dinámica
                    val nombreCreador = mapaUsuarios[dto.uuidUser1]?.usuario ?: "Jugador 1"
                    val nombreRetado = mapaUsuarios[dto.uuidUser2]?.usuario ?: ""

                    Reserva(
                        reservaUid = dto.id.toString(),
                        estado = estadoEnum.descripcion,
                        estadoColor = estadoEnum.colorHex,
                        estadoInt = estadoEnum.id,
                        sedeImagen = infoMaestra.sedeLogo,
                        sede = infoMaestra.sedeNombre,
                        sedeUid = infoMaestra.sedeId.toString(),
                        tipoJuego = dto.tipoJuego,
                        fechaInicio = dto.fechaInicio ?: "",
                        fechaFin = dto.fechaFin ?: "",
                        montoTotal = dto.montoTotalMonedas,
                        mesa = infoMaestra.mesaNombre,
                        creador = nombreCreador,
                        creadorUid = dto.uuidUser1 ?: "",
                        retado = nombreRetado,
                        retadoUid = dto.uuidUser2 ?: "",
                        userPendienteUid = dto.uuidUserPendiente ?: "",
                        juegoUid = "",
                        esperandoConfirmacion = dto.esperandoConfirmacion,
                        partidas = emptyList(),
                        ganadorUid = "",
                        userCreadorReady = dto.user1Ready,
                        userRetadoReady = dto.user2Ready
                    )
                } else null
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun responderReto(
        reservaId: String,
        mesa: String,
        sedeUid:String,
        aceptar: Boolean
    ): Result<String> {
        return try {
            val nuevoEstadoId = if (aceptar) 3 else 2
            val requestBody = AceptarRetoRequestDTO(
                estadoId = nuevoEstadoId,
                idReserva = reservaId,
                mesaDescripcion = mesa,
                idSede = sedeUid
            )
            val response =
                httpClient.put("${ConstatesCloud.URLBASE}apiPublic/public/reserva") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }
            return handleResponse<String, String>(response) { data ->
                Result.success(data)
            }

        } catch (e: Exception) {
            println("Error al responder al reto $reservaId: ${e.message}")
            Result.failure(Exception("No se pudo actualizar la respuesta del reto: ${e.message}"))
        }
    }

    override suspend fun eliminarReserva(reservaUid: String): Result<String> {
        return try {
            firestore.collection("Reserva")
                .document(reservaUid)
                .delete()

            Result.success("Reserva eliminada permanentemente")
        } catch (e: Exception) {
            println("Error al eliminar la reserva $reservaUid: ${e.message}")
            Result.failure(Exception("No se pudo eliminar el registro de la base de datos: ${e.message}"))
        }
    }

    override suspend fun updateEstadoReserva(
        reservaEstado: ReservaEstado,
        reservaUid: String
    ): Result<String> {
        return try {
            firestore.collection("Reserva")
                .document(reservaUid)
                .update(mapOf("uuidEstado" to reservaEstado.id))

            Result.success("Reserva actualizada permanentemente")
        } catch (e: Exception) {
            println("Error al actualizar el estado de la reserva $reservaUid: ${e.message}")
            Result.failure(Exception("No se pudo actualizar el estado de la reserva: ${e.message}"))
        }
    }
}