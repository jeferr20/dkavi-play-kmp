package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.firestore.FieldPath.Companion.documentId
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import pe.breaker.dkaviplay.data.mapper.mapToSede
import pe.breaker.dkaviplay.data.remote.firebase.EmpresaFirebase
import pe.breaker.dkaviplay.data.remote.firebase.SedeFirebase
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.domain.repository.SedeRepository
import kotlin.time.Clock
import kotlin.time.Instant

class SedeRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val sessionManager: UserSessionManager
) : SedeRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSedes(): Flow<Result<List<Sede>>> {
        return sessionManager.getCurrentUsuarioFlow()
            .flatMapLatest { usuario ->
                if (usuario == null) {
                    flowOf(Result.success(emptyList()))
                } else {
                    // Aquí es donde ocurre la magia reactiva
                    listenSedesByUbigeo(usuario.departamento,usuario.provincia)
                }
            }
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getSedesByDepartamento(departamento: String,provincia:String): Result<List<Sede>> {
        return fetchSedesByUbigeo(departamento,provincia,false)
    }

    private suspend fun fetchSedesByUbigeo(
        idDepartamento: String,
        idProvinicia:String,
        needEmpresa: Boolean
    ): Result<List<Sede>> {
        return try {
            val sedesSnapshot = firestore.collection("Sede")
                .where { "status" equalTo true }
                .where { "departamento" equalTo idDepartamento }
                .where { "provincia" equalTo idProvinicia }
                .get()

            if (sedesSnapshot.documents.isEmpty()) return Result.success(emptyList())

            val sedesData = sedesSnapshot.documents.map { it.id to it.data<SedeFirebase>() }

            if (!needEmpresa) {
                val simpleResult = sedesData.map { (id, dto) -> mapToSede(id, dto, null) }
                return Result.success(simpleResult)
            }

            val empresaIds = sedesData.mapNotNull { it.second.uuidEmpresa }.distinct()

            val empresasMap = if (empresaIds.isNotEmpty()) {
                firestore.collection("Empresa")
                    .where { documentId inArray empresaIds }
                    .get()
                    .documents
                    .associate { it.id to it.data<EmpresaFirebase>() }
            } else {
                emptyMap()
            }

            val result = sedesData.mapNotNull { (id, sedeDto) ->
                val empresaDto = empresasMap[sedeDto.uuidEmpresa]
                empresaDto?.let { mapToSede(id, sedeDto, it) }
            }

            Result.success(result)

        } catch (e: Exception) {
            println("Error FetchSedes: ${e.message}")
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun listenSedesByUbigeo(
        idDepartamento: String,
        idProvinicia:String
    ): Flow<Result<List<Sede>>> = firestore.collection("Sede")
        .where { "status" equalTo true }
        .where { "departamento" equalTo idDepartamento }
        .where { "provincia" equalTo idProvinicia }
        .snapshots()
        .mapLatest { sedesSnapshot ->
            try {
                if (sedesSnapshot.documents.isEmpty()) {
                    return@mapLatest Result.success(emptyList())
                }

                val sedesData = sedesSnapshot.documents.map { it.id to it.data<SedeFirebase>() }

                val empresaIds = sedesData.mapNotNull { it.second.uuidEmpresa }.distinct()

                val empresasMap = if (empresaIds.isNotEmpty()) {
                    firestore.collection("Empresa")
                        .where { documentId inArray empresaIds }
                        .get()
                        .documents
                        .associate { it.id to it.data<EmpresaFirebase>() }
                } else {
                    emptyMap()
                }

                val result = sedesData.mapNotNull { (id, sedeDto) ->
                    val empresaDto = empresasMap[sedeDto.uuidEmpresa]
                    empresaDto?.let { mapToSede(id, sedeDto, it) }
                }

                Result.success(result)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun getTarifaSede(sedeUid: String): Flow<Double?> = callbackFlow {
        val query = firestore.collection("TarifarioSede")
            .where { "uuidSede" equalTo sedeUid }
            .where { "status" equalTo true }

        val subscription = query.snapshots.onEach { querySnapshot ->
            val monto = querySnapshot.documents.firstOrNull()?.get<Double>("montoMesa")
            trySend(monto)
        }.catch { e ->
            println("Error trayendo tarifa: ${e.message}")
            trySend(null)
        }.launchIn(CoroutineScope(Dispatchers.IO))

        awaitClose { subscription.cancel() }
    }

    override fun getMesasSede(sedeUid: String): Flow<String> = callbackFlow {
        val ahora = Clock.System.now()

        // 1. Query optimizada (Requiere índice compuesto en Firebase)
        val queryReservas = firestore.collection("Reserva")
            .where { "uuidSede" equalTo sedeUid }
            .where { "status" equalTo true }
            .where { "fechaFin" greaterThan ahora.toFirebaseTimestamp() }

        val queryMesas = firestore.collection("Mesa")
            .where { "uuidSede" equalTo sedeUid }
            .where { "status" equalTo true }

        // 2. Usamos combine directamente y enviamos al canal del callbackFlow
        val job =
            combine(queryReservas.snapshots, queryMesas.snapshots) { resSnapshot, mesaSnapshot ->
                val currentInstant = Clock.System.now()

                // Usamos set para búsqueda O(1) más eficiente
                val mesasOcupadasIds = resSnapshot.documents.mapNotNull { doc ->
                    val inicio = doc.get<Timestamp>("fechaInicio")?.toKotlinInstant()
                    val fin = doc.get<Timestamp>("fechaFin")?.toKotlinInstant()
                    val mesaId = doc.get<String>("uuidMesa")

                    if (mesaId != null && inicio != null && fin != null && currentInstant in inicio..fin) {
                        mesaId
                    } else null
                }.toSet()

                val totalMesas = mesaSnapshot.documents.size
                val ocupadasCount = mesasOcupadasIds.size
                val mesasDisponibles = totalMesas - ocupadasCount

                "$mesasDisponibles/$totalMesas"
            }.onEach { result ->
                trySend(result) // Enviamos el String al colector
            }.launchIn(this) // <--- IMPORTANTE: Usa 'this' (el scope del callbackFlow)

        // 3. awaitClose es vital para limpiar recursos
        awaitClose {
            job.cancel()
        }
    }

    fun Timestamp.toKotlinInstant(): Instant {
        return Instant.fromEpochSeconds(this.seconds, this.nanoseconds)
    }

    fun Instant.toFirebaseTimestamp(): Timestamp {
        return Timestamp(this.epochSeconds, this.nanosecondsOfSecond)
    }
}