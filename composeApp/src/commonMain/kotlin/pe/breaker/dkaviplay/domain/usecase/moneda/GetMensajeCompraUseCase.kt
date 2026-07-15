package pe.breaker.dkaviplay.domain.usecase.moneda

import pe.breaker.dkaviplay.domain.repository.MonedaRepository

class GetMensajeCompraUseCase(
    private val repository: MonedaRepository
) {
    suspend operator fun invoke(cantidadMonedas: Int, usuario: String): Result<String> {
        return repository.getMensajeWhatsapp(usuario,cantidadMonedas)
    }
}