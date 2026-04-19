package pe.breaker.dkaviplay.data.repository

import dkaviplay.composeapp.generated.resources.Res
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.domain.model.UbigeoItem
import pe.breaker.dkaviplay.domain.repository.UbigeoRepository

class UbigeoRepositoryImpl(
    private val json: Json
) : UbigeoRepository {
    private var departamentos: List<UbigeoItem> = emptyList()
    private var provincias: Map<String, List<UbigeoItem>> = emptyMap()
    private var distritos: Map<String, List<UbigeoItem>> = emptyMap()

    suspend fun initData() {
        if(departamentos.isNotEmpty()) return
        try {
            coroutineScope {
                // 1. Iniciamos las 3 lecturas en paralelo
                val depsDeferred = async { Res.readBytes("files/departamentos.json").decodeToString() }
                val provDeferred = async { Res.readBytes("files/provincias.json").decodeToString() }
                val distDeferred = async { Res.readBytes("files/distritos.json").decodeToString() }

                // 2. Esperamos y parseamos los resultados
                // Nota: Asegúrate de que UbigeoItem sea @Serializable
                departamentos = json.decodeFromString<List<UbigeoItem>>(depsDeferred.await())
                provincias = json.decodeFromString<Map<String, List<UbigeoItem>>>(provDeferred.await())
                distritos = json.decodeFromString<Map<String, List<UbigeoItem>>>(distDeferred.await())
            }
            println("Ubigeo cargado exitosamente")
        } catch (e: Exception) {
            println("Error crítico cargando Ubigeo: ${e.message}")
            // Podrías lanzar una excepción personalizada aquí si es vital para la app
        }
    }

    override fun getDepartamentos() = departamentos
    override fun getProvincias(depId: String) = provincias[depId] ?: emptyList()
    override fun getDistritos(provId: String) = distritos[provId] ?: emptyList()
}