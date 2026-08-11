package pe.breaker.dkaviplay.domain.usecase.reserva

import pe.breaker.dkaviplay.data.remote.supabase.rpc.ValidarHoraReservaDTO
import pe.breaker.dkaviplay.domain.repository.ReservaRepository

class ValidarHoraReservaUseCase(private val respository: ReservaRepository) {
    suspend operator fun invoke(reservaUid: String): Result<ValidarHoraReservaDTO> {
        return respository.verificarHoraReserva(reservaUid)
    }
}