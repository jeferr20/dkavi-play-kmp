package pe.breaker.dkaviplay.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import pe.breaker.dkaviplay.data.remote.supabase.MesaDTO
import pe.breaker.dkaviplay.domain.model.Mesa
import pe.breaker.dkaviplay.domain.repository.MesaRepository

class MesaRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : MesaRepository {
    override suspend fun getMesasBySede(sedeUid: String): Result<List<Mesa>> {
        return try {
            val sedeIdInt = sedeUid.toIntOrNull() ?: return Result.success(emptyList())

            val mesasList = supabaseClient
                .from(schema = "public", table = "Mesa")
                .select{
                    filter {
                        eq("status",true)
                        eq("id_sede",sedeIdInt)
                    }
                    order("descripcion", Order.ASCENDING)
                }.decodeList<MesaDTO>()

            val mesas = mesasList.map { mesa->
                Mesa(
                    mesaUid = mesa.id.toString(),
                    nombreMesa = mesa.descripcion
                )
            }
            Result.success(mesas)
        }catch (e: Exception) {
            println("Error GET MESAS: ${e.message}")
            Result.failure(e)
        }
    }
}