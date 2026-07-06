package pe.breaker.dkaviplay.core.domain.repository

import pe.breaker.dkaviplay.core.domain.model.UbigeoItem

interface UbigeoRepository {
    fun getDepartamentos(): List<UbigeoItem>
    fun getProvincias(depId: String): List<UbigeoItem>
    fun getDistritos(provId: String): List<UbigeoItem>
}