package pe.breaker.dkaviplay.data.remote.supabase.view

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pe.breaker.dkaviplay.data.remote.supabase.HorarioSedeDTO

@Serializable
data class SedeEmpresaView(
    @SerialName("sedeid") val sedeId: Int,
    @SerialName("empresaid") val empresaId: Int,
    @SerialName("nombresede") val nombreSede: String,
    val direccion: String?,
    @SerialName("numsede") val numSede: String?,
    val referencia: String?,
    @SerialName("nombreempresa") val nombreEmpresa: String,
    val logo: String?,
    val ruc: String?,
    val latitud: Double,
    val longitud: Double,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val horarios: List<HorarioSedeDTO> = emptyList()
)