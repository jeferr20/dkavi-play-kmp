package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import io.github.jan.supabase.realtime.selectSingleValueAsFlow
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import pe.breaker.dkaviplay.data.entity.UsuarioConInventario
import pe.breaker.dkaviplay.data.mapper.toEntity
import pe.breaker.dkaviplay.data.remote.dto.AgregarInventarioRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.ItemInventarioRequestDTO
import pe.breaker.dkaviplay.data.remote.dto.UserMovilDto
import pe.breaker.dkaviplay.data.remote.supabase.InventarioDTO
import pe.breaker.dkaviplay.data.remote.supabase.UsuarioHorarioDTO
import pe.breaker.dkaviplay.data.util.ConstatesCloud
import pe.breaker.dkaviplay.data.util.handleResponse
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.domain.repository.UsuarioRepository

class UsuarioRepositoryImpl(
    private val httpClient: HttpClient,
    private val supabase: SupabaseClient,
) : UsuarioRepository {

    @OptIn(SupabaseExperimental::class)
    override fun getUsuarioStream(usuarioUid: String,userId: Int): Flow<UsuarioConInventario?> {
        val usuarioFlow = supabase
            .from(schema = "seguridad", table = "UserMovil")
            .selectSingleValueAsFlow(
                primaryKey = UserMovilDto::id,
                channelName = "seguridad:UserMovil:$usuarioUid",
                filter = { UserMovilDto::uuidAuth eq usuarioUid }
            )

        val inventarioFlow = supabase
            .from(schema = "seguridad", table = "UserMovilInventario")
            .selectAsFlow(
                primaryKey = InventarioDTO::id,
                channelName = "seguridad:UserMovilInventario:$userId",
                filter = FilterOperation("usermovil_id", FilterOperator.EQ, userId)
            )

        val horarioFlow = supabase
            .from(schema = "seguridad", table = "UserMovilHorario")
            .selectAsFlow(
                primaryKey = UsuarioHorarioDTO::id,
                channelName = "seguridad:UserMovilHorario:$userId",
                filter = FilterOperation("usermovil_id", FilterOperator.EQ, userId)
            )

        return combine(usuarioFlow, inventarioFlow,horarioFlow) { userDto, listaInventarioDto, listHorarioDto ->
            try {
                println("Realtime Switch ➡️ User cambió: ${userDto != null}, Inv Size: ${listaInventarioDto.size}, Horario Size: ${listHorarioDto.size}")
                val userEntity = userDto?.toEntity() ?: return@combine null
                val inventario = listaInventarioDto.map { it.toEntity() }
                val horarios = listHorarioDto.map { it.toEntity() }

                UsuarioConInventario(
                    usuario = userEntity,
                    inventario = inventario,
                    horario = horarios
                )
            } catch (e: Exception) {
                println("Error mapeando datos combinados de Supabase: ${e.message}")
                null
            }
        }
            .distinctUntilChanged()
            .catch { e ->
                println("Error en el Stream combinado de Supabase: ${e.message}")
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