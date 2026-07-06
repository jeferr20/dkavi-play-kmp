package pe.breaker.dkaviplay.core.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pe.breaker.dkaviplay.core.cache.Database
import pe.breaker.dkaviplay.core.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.core.data.mapper.toEntity
import pe.breaker.dkaviplay.core.data.remote.dto.AgregarInventarioRequestDTO
import pe.breaker.dkaviplay.core.data.remote.dto.ItemInventarioRequestDTO
import pe.breaker.dkaviplay.core.data.remote.firebase.UserMovilFirebase
import pe.breaker.dkaviplay.core.data.util.ConstatesCloud
import pe.breaker.dkaviplay.core.data.util.handleResponse
import pe.breaker.dkaviplay.core.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.core.domain.repository.UsuarioRepository

class UsuarioRepositoryImpl(
    private val httpClient: HttpClient,
    private val firestore: Lazy<FirebaseFirestore>,
    private val database: Database,
) : UsuarioRepository {

    override fun getUsuarioStream(usuarioUid: String): Flow<UsuarioEntity?> {
        return firestore.value
            .collection("UserMovil")
            .document(usuarioUid)
            .snapshots
            .map { snapshot ->
                if (snapshot.exists) {
                    try {
                        val usuarioDto = snapshot.data<UserMovilFirebase>()
                        val entity = usuarioDto
                            .copy(userUid = snapshot.id)
                            .toEntity()

                        withContext(Dispatchers.IO) {
                            database.insertUsuarioTable(entity)
                        }

                        entity
                    } catch (e: Exception) {
                        println("Error en Firestore Stream Usuario Snapshot: ${e.message}")
                        null
                    }
                } else {
                    null
                }
            }
            .distinctUntilChanged()
            .catch { e ->
                println("Error en Firestore Stream Usuario: ${e.message}")
                emit(null)
            }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun otorgarRecompensas(
        userId: String,
        recompensas: List<DetallePremio>
    ): Result<String> {
        val itemsAgrupados = recompensas
            .groupBy { it.id }
            .map { (id, listaDeMismoPremio) ->
                ItemInventarioRequestDTO(
                    id = id,
                    cantidad = listaDeMismoPremio.sumOf {
                        if (it.cantidad <= 0) 1 else it.cantidad
                    }
                )
            }
        val request = AgregarInventarioRequestDTO(
            userUid = userId,
            listaInventario = itemsAgrupados,
            reservaId = null,
            isCreador = null
        )

        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/inventario") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }

    override suspend fun usarRecompensas(
        userId: String,
        itemId: Int,
        reservaId: String,
        isCreador: Boolean
    ): Result<String> {
        val item = ItemInventarioRequestDTO(
            id = itemId,
            cantidad = -1
        )
        val lista = mutableListOf<ItemInventarioRequestDTO>()
        lista.add(item)
        val request = AgregarInventarioRequestDTO(
            userUid = userId,
            listaInventario = lista,
            reservaId = reservaId,
            isCreador = isCreador
        )
        val response = httpClient.post("${ConstatesCloud.URLBASE}apiPublic/public/inventario") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return handleResponse<String, String>(response) { data ->
            Result.success(data)
        }
    }
}