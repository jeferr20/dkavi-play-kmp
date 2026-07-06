package pe.breaker.dkaviplay.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.core.domain.model.Sede

interface SedeRepository {
    fun getSedes(): Flow<Result<List<Sede>>>
    suspend fun getSedesByDepartamento(departamento: String, provincia: String): Result<List<Sede>>
    fun getTarifaSede(sedeUid: String): Flow<Double?>
    fun getMesasSede(sedeUid: String): Flow<String>
}