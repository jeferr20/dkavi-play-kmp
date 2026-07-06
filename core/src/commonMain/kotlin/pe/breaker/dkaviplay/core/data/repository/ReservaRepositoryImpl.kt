package pe.breaker.dkaviplay.core.data.repository

import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import pe.breaker.dkaviplay.core.data.mapper.mapToReserva
import pe.breaker.dkaviplay.core.data.remote.dto.AceptarRetoRequestDTO
import pe.breaker.dkaviplay.core.data.remote.dto.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.core.data.remote.firebase.JuegoFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.MesaFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.ReservaFirebase
import pe.breaker.dkaviplay.core.data.remote.firebase.SedeFirebase
import pe.breaker.dkaviplay.core.data.util.ConstatesCloud
import pe.breaker.dkaviplay.core.data.util.handleResponse
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.core.domain.model.ReservaEstado
import pe.breaker.dkaviplay.core.domain.repository.ReservaRepository
import pe.breaker.dkaviplay.core.domain.repository.TimeRepository
import kotlin.time.ExperimentalTime

class ReservaRepositoryImpl(
    private val httpClient: HttpClient,
    private val firestore: FirebaseFirestore,
    private val timeRepository: TimeRepository
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

    @OptIn(ExperimentalTime::class)
    override fun getReservasFlow(usuarioUid: String?): Flow<List<Reserva>> {
        val userUid = usuarioUid ?: return flowOf(emptyList())

        // 1. Usamos flow {} como constructor para poder llamar a funciones suspendidas
        return flow {
            // Aquí sí podemos llamar a getServerTime() porque estamos dentro de un bloque suspendido
            val serverTime = timeRepository.getServerTime()
            val hace30Dias = serverTime.minus(30, DateTimeUnit.DAY, TimeZone.of("America/Lima"))
            val timestampCorte = Timestamp(hace30Dias.epochSeconds, 0)

            // 2. Definimos las queries con el filtro de tiempo
            val flow1 = firestore.collection("Reserva")
                .where { "status" equalTo true }
                .where { "uuidUser1" equalTo userUid }
                .where { "fechaInicio" greaterThanOrEqualTo timestampCorte }
                .snapshots

            val flow2 = firestore.collection("Reserva")
                .where { "status" equalTo true }
                .where { "uuidUser2" equalTo userUid }
                .where { "fechaInicio" greaterThanOrEqualTo timestampCorte }
                .snapshots

            // 3. Combinamos y emitimos los cambios en tiempo real
            combine(flow1, flow2) { snap1, snap2 ->
                val allDocs = (snap1.documents + snap2.documents).distinctBy { it.id }
                allDocs
            }.map { docs ->
                processReservasDocuments(docs)
            }.collect { reservas ->
                emit(reservas)
            }
        }.flowOn(Dispatchers.IO)
    }

    // Función auxiliar para no saturar el flujo principal
    private suspend fun processReservasDocuments(docs: List<DocumentSnapshot>): List<Reserva> {
        if (docs.isEmpty()) return emptyList()

        val docsData = docs.map { it.id to it.data<ReservaFirebase>() }
        val sedeIds = docsData.mapNotNull { it.second.uuidSede }.distinct()
        val mesasIds = docsData.mapNotNull { it.second.uuidMesa }.distinct()

        // Consultas paralelas para sedes y mesas (Optimizado con async)
        return coroutineScope {
            val sedesDeferred = sedeIds.map { id ->
                async { id to firestore.collection("Sede").document(id).get().data<SedeFirebase>() }
            }
            val mesasDeferred = mesasIds.map { id ->
                async { id to firestore.collection("Mesa").document(id).get().data<MesaFirebase>() }
            }

            val sedesMap = sedesDeferred.awaitAll().toMap()
            val mesasMap = mesasDeferred.awaitAll().toMap()

            docsData.mapNotNull { (docId, fb) ->
                val sede = sedesMap[fb.uuidSede]
                val mesa = mesasMap[fb.uuidMesa]
                if (sede != null && mesa != null) {
                    mapToReserva(docId, sede, fb, mesa, null)
                } else null
            }.sortedByDescending { it.fechaInicio }
        }
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