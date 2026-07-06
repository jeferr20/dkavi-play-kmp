package pe.breaker.dkaviplay.core.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.core.domain.model.UbigeoItem
import pe.breaker.dkaviplay.core.domain.repository.UbigeoRepository

class UbigeoRepositoryImpl(
    private val json: Json,
    private val loadJsonResource: suspend (path: String) -> String
) : UbigeoRepository {
    private var departamentos: List<UbigeoItem> = emptyList()
    private var provincias: Map<String, List<UbigeoItem>> = emptyMap()
    private var distritos: Map<String, List<UbigeoItem>> = emptyMap()

    suspend fun initData() {
        if(departamentos.isNotEmpty()) return
        try {
            coroutineScope {
                val depsDeferred = async { loadJsonResource("files/departamentos.json") }
                val provDeferred = async { loadJsonResource("files/provincias.json") }
                val distDeferred = async {loadJsonResource("files/distritos.json")}

                departamentos = json.decodeFromString<List<UbigeoItem>>(depsDeferred.await())
                provincias = json.decodeFromString<Map<String, List<UbigeoItem>>>(provDeferred.await())
                distritos = json.decodeFromString<Map<String, List<UbigeoItem>>>(distDeferred.await())
            }
            println("Ubigeo cargado exitosamente")
        } catch (e: Exception) {
            println("Error crítico cargando Ubigeo: ${e.message}")
        }
    }

    override fun getDepartamentos() = departamentos
    override fun getProvincias(depId: String) = provincias[depId] ?: emptyList()
    override fun getDistritos(provId: String) = distritos[provId] ?: emptyList()
}