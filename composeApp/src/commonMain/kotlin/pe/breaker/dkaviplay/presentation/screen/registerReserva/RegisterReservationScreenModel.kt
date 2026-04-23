package pe.breaker.dkaviplay.presentation.screen.registerReserva

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.data.remote.dto.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Mesa
import pe.breaker.dkaviplay.domain.model.TipoJuego
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.repository.SedeRepository
import pe.breaker.dkaviplay.domain.usecase.GetMesaUseCase
import pe.breaker.dkaviplay.domain.usecase.RegisterReservationUseCase
import pe.breaker.dkaviplay.presentation.util.createTimestampDTO
import pe.breaker.dkaviplay.presentation.util.formatMillisToDate
import kotlin.time.Clock

class RegisterReservationScreenModel(
    private val registerReservationUseCase: RegisterReservationUseCase,
    private val getMesasUseCase: GetMesaUseCase,
    private val sessionManager: UserSessionManager,
    private val sedeRepository: SedeRepository,
    private val sedeUid: String?
) : StateScreenModel<RegisterReservationScreenState>(RegisterReservationScreenState()){

    init {
        listenToTarifa()
        getMesas()
    }

    fun onTipoJuegoSelected(tipo: TipoJuego) {
        mutableState.update { it.copy(tipoJuego = tipo) }
    }

    fun onUsuarioRetadoSelected(usuario : UserQuick) {
        if (state.value.usuarioRetadoUid == usuario.userUid) return
        mutableState.update { it.copy(usuarioRetado = usuario.usuario, usuarioRetadoUid = usuario.userUid) }
    }

    fun onDateSelected(dateMillis: Long?, isStart: Boolean) {
        dateMillis?.let {
            val dateString = formatMillisToDate(it) // Implementar función de formato
            mutableState.update { state ->
                if (isStart) state.copy(fInicio = dateString)
                else state.copy(fSalida = dateString)
            }
            validateAndCalculate()
        }
    }

    fun onTimeSelected(hour: Int, minute: Int, isStart: Boolean) {
        val timeString = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
        mutableState.update { state ->
            if (isStart) state.copy(hInicio = timeString)
            else state.copy(hSalida = timeString)
        }
        validateAndCalculate()
    }

    fun onMesaSelected(mesa: Mesa) {
        mutableState.update { it.copy(selectedMesa = mesa) }
    }

    private fun getMesas() {
        if (sedeUid == null) return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoadingMesa = true, errorMessage = null) }
            getMesasUseCase(sedeUid)
                .onSuccess { mesas ->
                    val listaConAutomatica = mesas.toMutableList()
                    val mesaAutomatica = Mesa(
                        mesaUid = "AUTO",
                        nombreMesa = "Automática",
                    )
                    listaConAutomatica.add(0, mesaAutomatica)
                    mutableState.update { it.copy(isLoadingMesa = false, mesas = listaConAutomatica, selectedMesa = listaConAutomatica.first()) }
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoadingMesa = false, errorMessage = error.message) }
                }
        }
    }

    private fun listenToTarifa() {
        sedeUid?.let {
            screenModelScope.launch {
                sedeRepository.getTarifaSede(it).collect { tarifa ->
                    mutableState.update { it.copy(precioPorHora = tarifa ?: 0.0) }
                    validateAndCalculate()
                }
            }
        }
    }

    private fun validateAndCalculate() {
        val s = state.value

        // Solo validamos si los 4 campos están presentes
        if (s.fInicio != null && s.hInicio != null && s.fSalida != null && s.hSalida != null) {
            try {
                val inicioSeconds = createTimestampDTO(s.fInicio, s.hInicio).seconds
                val finSeconds = createTimestampDTO(s.fSalida, s.hSalida).seconds
                val ahoraSeconds = Clock.System.now().epochSeconds
                val diferenciaSegundos = finSeconds - inicioSeconds

                // 1. ¿Es en el pasado? (Margen de 1 minuto para evitar errores por segundos)
                if (inicioSeconds < (ahoraSeconds - 60)) {
                    mutableState.update { it.copy(errorMessage = "La fecha de inicio no puede ser pasada.", montoReserva = 0.0) }
                    return
                }

                // 2. ¿Orden cronológico?
                if (finSeconds <= inicioSeconds) {
                    mutableState.update { it.copy(errorMessage = "La salida debe ser después del inicio.", montoReserva = 0.0) }
                    return
                }

                // 3. ¿Mínimo 30 minutos?
                if (diferenciaSegundos < 30 * 60) {
                    mutableState.update { it.copy(errorMessage = "La reserva mínima es de 30 min.", montoReserva = 0.0) }
                    return
                }

                // 4. ¿Máximo 18 horas?
                if (diferenciaSegundos > 18 * 3600) {
                    mutableState.update { it.copy(errorMessage = "La reserva máxima es de 18 horas.", montoReserva = 0.0) }
                    return
                }

                val horas = diferenciaSegundos.toDouble() / 3600.0
                val costoHoras = horas * (s.precioPorHora ?: 0.0)
                val comisionTotal = 5.0 * 2
                val totalFinal = costoHoras + comisionTotal

                mutableState.update { it.copy(
                    errorMessage = null,
                    montoReserva = totalFinal,
                ) }

            } catch (e: Exception) {
                mutableState.update { it.copy(errorMessage = "Formato de fecha inválido") }
            }
        }
    }

    fun onSaveReserva() {
        val s = state.value
        val usuario = sessionManager.getCurrentUsuario()

        if (usuario == null) {
            mutableState.update { it.copy(errorMessage = "Sesión expirada.") }
            return
        }

        screenModelScope.launch {
            try {
                mutableState.update { it.copy(isLoading = true, errorMessage = null) }

                val mesaUidFinal = if (s.selectedMesa?.mesaUid == "AUTO") {
                    null
                } else {
                    s.selectedMesa?.mesaUid
                }

                val request = RegisterReservaRequestDTO(
                    sedeUid = sedeUid ?: "",
                    fechaHoraInicio = createTimestampDTO(s.fInicio!!, s.hInicio!!),
                    fechaHoraFin = createTimestampDTO(s.fSalida!!, s.hSalida!!),
                    montoTotal = s.montoReserva ?: 0.0,
                    uuidUser1 = usuario.usuarioUid,
                    user1 = usuario.usuario,
                    mesaUid = mesaUidFinal,
                    uuidUser2 = s.usuarioRetadoUid ?: "",
                    user2 = s.usuarioRetado ?: "",
                    tipoJuego = s.tipoJuego.nombre
                )

                registerReservationUseCase(request).fold(
                    onSuccess = { mensaje ->
                        mutableState.update { it.copy(isLoading = false, isSuccess = true, successMessage = mensaje) }
                    },
                    onFailure = { error ->
                        mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
                )
            } catch (e: Exception) {
                mutableState.update { it.copy(isLoading = false, errorMessage = "Error al procesar reserva.") }
            }
        }
    }

    fun calcularDuracion(): String {
        val s = state.value
        if (s.fInicio != null && s.hInicio != null && s.fSalida != null && s.hSalida != null) {
            val inicio = createTimestampDTO(s.fInicio, s.hInicio).seconds
            val fin = createTimestampDTO(s.fSalida, s.hSalida).seconds
            val diffSeconds = fin - inicio

            if (diffSeconds <= 0) return "--"

            val totalMinutos = diffSeconds / 60
            val horas = totalMinutos / 60
            val minutos = totalMinutos % 60

            return buildString {
                if (horas > 0) append("${horas}h ")
                if (minutos > 0) append("${minutos}min")
                if (horas == 0L && minutos == 0L) append("0min")
            }.trim()
        }
        return "--"
    }

    fun clearError() = mutableState.update { it.copy(errorMessage = null) }
    fun clearSuccess() = mutableState.update { it.copy(isSuccess = false, successMessage = null) }

}