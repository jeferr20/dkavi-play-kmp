package pe.breaker.dkaviplay.domain.repository

import pe.breaker.dkaviplay.domain.model.UbigeoItem

interface UbigeoRepository {
    fun getDepartamentos(): List<UbigeoItem>
    fun getProvincias(depId: String): List<UbigeoItem>
    fun getDistritos(provId: String): List<UbigeoItem>
}