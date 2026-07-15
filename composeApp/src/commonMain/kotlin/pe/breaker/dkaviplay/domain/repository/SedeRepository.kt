package pe.breaker.dkaviplay.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.domain.model.Sede

interface SedeRepository {
    fun getSedes(): Flow<Result<List<Sede>>>
    suspend fun getSedesByDepartamento(departamento: String, provincia: String): Result<List<Sede>>
    suspend fun getTarifaSede(sedeUid: String): Result<Double?>
    fun getMesasSede(sedeUid: String): Flow<String>
}