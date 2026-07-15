package pe.breaker.dkaviplay.domain.usecase.moneda

import pe.breaker.dkaviplay.data.remote.supabase.view.TarifarioView
import pe.breaker.dkaviplay.domain.repository.MonedaRepository

class GetTarifaMonedaUseCase(
    private val monedaRepository: MonedaRepository
) {
    suspend operator fun invoke(sedeUid:Int) : Result<TarifarioView> {
        return monedaRepository.getPrecioMonedas(sedeUid)
    }
}