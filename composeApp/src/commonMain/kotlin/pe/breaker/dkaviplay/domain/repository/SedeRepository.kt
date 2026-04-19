package pe.breaker.dkaviplay.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.domain.model.Sede

interface SedeRepository {
    fun getSedes(): Flow<Result<List<Sede>>>
    suspend fun getSedesByDepartamento(departamento: String): Result<List<Sede>>
    fun getTarifaSede(sedeUid: String): Flow<Double?>
    fun getMesasSede(sedeUid: String): Flow<String>
}