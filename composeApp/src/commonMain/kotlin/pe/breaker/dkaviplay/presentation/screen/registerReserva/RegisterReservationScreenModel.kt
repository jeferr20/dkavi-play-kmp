package pe.breaker.dkaviplay.presentation.screen.registerReserva

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pe.breaker.dkaviplay.data.remote.dto.RegisterReservaRequestDTO
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.Mesa
import pe.breaker.dkaviplay.domain.model.TipoJuego
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.domain.repository.SedeRepository
import pe.breaker.dkaviplay.domain.usecase.GetMesaUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.RegisterReservationUseCase
import pe.breaker.dkaviplay.util.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class RegisterReservationScreenModel(
    private val registerReservationUseCase: RegisterReservationUseCase,
    private val getMesasUseCase: GetMesaUseCase,
    private val sessionManager: UserSessionManager,
    private val sedeRepository: SedeRepository,
    private val dateTimeFormatter: DateTimeFormatter,
    private val sedeUid: String?
) : StateScreenModel<RegisterReservationScreenState>(RegisterReservationScreenState()){

    init {
        cargarTarifa()
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
            val dateString = dateTimeFormatter.formatMillisToDate(it)
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
        val uid = sedeUid ?: return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoadingMesa = true, errorMessage = null) }
            getMesasUseCase(uid)
                .onSuccess { mesas ->
                    val listaConAutomatica = mesas.toMutableList().apply {
                        add(0, Mesa(mesaUid = "AUTO", nombreMesa = "Automática"))
                    }
                    mutableState.update {
                        it.copy(
                            isLoadingMesa = false,
                            mesas = listaConAutomatica,
                            selectedMesa = listaConAutomatica.first()
                        )
                    }
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoadingMesa = false, errorMessage = error.message) }
                }
        }
    }

    private fun cargarTarifa() {
        val uid = sedeUid ?: return
        screenModelScope.launch {
            sedeRepository.getTarifaSede(uid)
                .onSuccess { tarifa ->
                    mutableState.update { it.copy(tarifario = tarifa ?: 0.0) }
                    validateAndCalculate()
                }
                .onFailure { error ->
                    println("❌ Error al cargar tarifa: ${error.message}")
                    mutableState.update { it.copy(tarifario = 0.0) }
                }
        }
    }

    fun onImmediateToggled(enabled: Boolean) {
        mutableState.update { state ->
            if (enabled) {
                val timeZone = TimeZone.currentSystemDefault()
                val nowLocal = Clock.System.now().toLocalDateTime(timeZone)

                // 1. Formatear la hora local HH:mm (ej: "19:38")
                val timeString = "${nowLocal.hour.toString().padStart(2, '0')}:${nowLocal.minute.toString().padStart(2, '0')}"

                // 2. Formatear la fecha DIRECTAMENTE desde los componentes locales (dd/MM/yyyy)
                val day = nowLocal.dayOfMonth.toString().padStart(2, '0')
                val month = nowLocal.monthNumber.toString().padStart(2, '0')
                val year = nowLocal.year

                val dateString = "$day/$month/$year" // Genera directamente "11/08/2026"

                state.copy(
                    isImmediate = true,
                    fInicio = dateString,
                    hInicio = timeString,
                    fSalida = null,
                    hSalida = null
                )
            } else {
                state.copy(isImmediate = false)
            }
        }
        validateAndCalculate()
    }

    private fun validateAndCalculate() {
        val s = state.value

        // 1. Validar que al menos Fecha y Hora de Inicio estén seleccionadas
        if (s.fInicio == null || s.hInicio == null) return

        try {
            val inicioInstant = dateTimeFormatter.parseToInstant(s.fInicio, s.hInicio)
            val ahoraInstant = Clock.System.now()

            // 2. ¿La fecha/hora de inicio es en el pasado? (Tolera 1 min)
            if (inicioInstant < (ahoraInstant - 1.minutes)) {
                mutableState.update { it.copy(errorMessage = "La fecha de inicio no puede ser pasada.") }
                return
            }

            // 3. Si ingresó Fecha y Hora de Salida, validamos rango y duración
            if (s.fSalida != null && s.hSalida != null) {
                val finInstant = dateTimeFormatter.parseToInstant(s.fSalida, s.hSalida)
                val duracion = finInstant - inicioInstant

                // ¿Orden cronológico?
                if (finInstant <= inicioInstant) {
                    mutableState.update { it.copy(errorMessage = "La salida debe ser después del inicio.") }
                    return
                }

                // ¿Mínimo 30 minutos?
                if (duracion < 30.minutes) {
                    mutableState.update { it.copy(errorMessage = "La reserva mínima es de 30 min.") }
                    return
                }

                // ¿Máximo 18 horas?
                if (duracion > 18.hours) {
                    mutableState.update { it.copy(errorMessage = "La reserva máxima es de 18 horas.") }
                    return
                }
            }

            mutableState.update { it.copy(errorMessage = null) }

        } catch (e: Exception) {
            mutableState.update { it.copy(errorMessage = "Formato de fecha inválido") }
        }
    }

    fun onSaveReserva() {
        val s = state.value
        val usuario = sessionManager.getCurrentUsuario()

        if (usuario == null) {
            mutableState.update { it.copy(errorMessage = "Sesión expirada.") }
            return
        }

        if (s.fInicio == null || s.hInicio == null) {
            mutableState.update { it.copy(errorMessage = "Selecciona la hora de inicio.") }
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

                val fechaHoraFinFinal = if (!s.fSalida.isNullOrBlank() && !s.hSalida.isNullOrBlank()) {
                    dateTimeFormatter.toIsoStringWithOffset(s.fSalida, s.hSalida)
                } else {
                    null
                }

                val request = RegisterReservaRequestDTO(
                    sedeUid = sedeUid?.toInt() ?: 0,
                    fechaHoraInicio = dateTimeFormatter.toIsoStringWithOffset(s.fInicio, s.hInicio),
                    fechaHoraFin = fechaHoraFinFinal,
                    montoTotal = s.tarifario ?: 0.0,
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
            return try {
                val startInstant = dateTimeFormatter.parseToInstant(s.fInicio, s.hInicio)
                val endInstant = dateTimeFormatter.parseToInstant(s.fSalida, s.hSalida)

                val duration = endInstant - startInstant

                if (duration.isNegative()) return "--"
                if (duration.inWholeMinutes == 0L) return "0min"

                val totalMinutos = duration.inWholeMinutes
                val horas = totalMinutos / 60
                val minutos = totalMinutos % 60

                buildString {
                    if (horas > 0) append("${horas}h ")
                    if (minutos > 0) append("${minutos}min")
                }.trim()
            } catch (e: Exception) {
                "--"
            }
        }
        return "--"
    }

    fun clearError() = mutableState.update { it.copy(errorMessage = null) }
    fun clearSuccess() = mutableState.update { it.copy(isSuccess = false, successMessage = null) }
}