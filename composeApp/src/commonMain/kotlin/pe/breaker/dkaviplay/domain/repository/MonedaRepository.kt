package pe.breaker.dkaviplay.domain.repository

import pe.breaker.dkaviplay.data.remote.dto.request.LogMonedaDTO
import pe.breaker.dkaviplay.data.remote.supabase.view.TarifarioView

interface MonedaRepository {
    suspend fun getPrecioMonedas(sedeUid:Int): Result<TarifarioView>
    suspend fun registroLogMonedas(body: LogMonedaDTO) : Result<Unit>
    suspend fun getMensajeWhatsapp(userUid:String, monedas:Int) : Result<String>
}