package pe.breaker.dkaviplay.domain.usecase.moneda

import pe.breaker.dkaviplay.data.remote.dto.request.LogMonedaDTO
import pe.breaker.dkaviplay.domain.repository.MonedaRepository

class InsertMonedaLogUseCase(
    private val monedaRepository: MonedaRepository
) {
    suspend operator fun invoke(body:LogMonedaDTO) : Result<Unit> {
        return monedaRepository.registroLogMonedas(body)
    }
}