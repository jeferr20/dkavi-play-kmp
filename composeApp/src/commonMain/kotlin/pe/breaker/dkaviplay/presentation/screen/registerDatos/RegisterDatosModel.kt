package pe.breaker.dkaviplay.presentation.screen.registerDatos

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.data.mapper.toDTO
import pe.breaker.dkaviplay.data.mapper.toState
import pe.breaker.dkaviplay.data.remote.dto.RegisterUsuarioRequestDto
import pe.breaker.dkaviplay.data.remote.firebase.UserHorarioFirebase
import pe.breaker.dkaviplay.data.repository.UbigeoRepositoryImpl
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.repository.UbigeoRepository
import pe.breaker.dkaviplay.domain.usecase.GetSedesByUbigeoUseCase
import pe.breaker.dkaviplay.domain.usecase.RegisterPersonaUseCase

class RegisterDatosModel(
    private val usuarioUid: String?,
    private val isLogged: Boolean,
    private val registerPersonaUseCase: RegisterPersonaUseCase,
    private val ubigeoRepository: UbigeoRepository,
    private val getSedesByUbigeoUseCase: GetSedesByUbigeoUseCase,
    private val validator: RegisterDatosValidator,
    private val sessionManager: UserSessionManager,
) : StateScreenModel<RegisterDatosState>(RegisterDatosState()) {

    init {
        if (isLogged) {
            loadExistingUserData()
        }
        loadData()
    }

    private fun loadExistingUserData() {
        val persona = sessionManager.getCurrentPersona()
        val usuario = sessionManager.getCurrentUsuario()
        if (persona != null && usuario != null) {
            val horario = usuario.horariosJson.let { jsonStr ->
                try {
                    Json.decodeFromString<List<UserHorarioFirebase>>(jsonStr)
                } catch (e: Exception) {
                    null
                }
            }

            if (horario != null) {
                updateState {
                    copy(
                        nombres = persona.nombres,
                        apellidoPaterno = persona.apellidoPaterno,
                        apellidoMaterno = persona.apellidoMaterno,
                        correo = persona.correo,
                        celular = persona.celular,
                        genero = usuario.genero,
                        idSedeSeleccionada = usuario.sedePreferencia,
                        idDepartamento = usuario.departamento,
                        idProvincia = usuario.provincia,
                        idDistrito = usuario.distrito,
                        personaUid = persona.personaUid,
                        horarios = horario.map { it.toState() }
                    )
                }
            }
        }
    }

    private fun loadData() {
        screenModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            try {
                (ubigeoRepository as? UbigeoRepositoryImpl)?.initData()
                val departamentos = ubigeoRepository.getDepartamentos()

                if (isLogged) {
                    val s = state.value
                    val provs =
                        s.idDepartamento?.let { ubigeoRepository.getProvincias(it) } ?: emptyList()
                    val dists =
                        s.idProvincia?.let { ubigeoRepository.getDistritos(it) } ?: emptyList()

                    val sedesResult = s.idDepartamento?.let { departamento ->
                        s.idProvincia?.let { provincia ->
                            getSedesByUbigeoUseCase(departamento, provincia)
                        }
                    }

                    updateState {
                        copy(
                            isLoading = false,
                            departamentos = departamentos,
                            provincias = provs,
                            distritos = dists,
                            sedes = sedesResult?.getOrDefault(emptyList()) ?: emptyList()
                        )
                    }
                } else {
                    updateState {
                        copy(
                            isLoading = false,
                            departamentos = departamentos
                        )
                    }
                }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun onFieldChanged(action: RegisterDatosState.() -> RegisterDatosState) {
        mutableState.update { it.action() }
    }

    fun onHorarioChanged(index: Int, nuevoDia: DiaHorarioState) {
        if (nuevoDia.horaInicio.length > 5 || nuevoDia.horaFin.length > 5) return

        mutableState.update { state ->
            val nuevaLista = state.horarios.toMutableList()
            nuevaLista[index] = nuevoDia
            state.copy(horarios = nuevaLista, horarioError = null)
        }
    }

    fun onDepartamentoSelected(id: String) {
        val provs = ubigeoRepository.getProvincias(id)
        updateState {
            copy(
                idDepartamento = id,
                provincias = provs,
                idProvincia = null,
                idDistrito = null,
                distritos = emptyList(),
                sedes = emptyList(),
                idSedeSeleccionada = null,
                sedeError = null,
                departamentoError = null
            )
        }
    }

    private fun loadSedes(idDepartamento: String, idProvincia: String) {
        screenModelScope.launch {
            val result = getSedesByUbigeoUseCase(idDepartamento, idProvincia)
            updateState {
                copy(sedes = result.getOrDefault(emptyList()))
            }
        }
    }

    fun onProvinciaSelected(id: String) {
        val currentDepartamento = state.value.idDepartamento ?: return
        val dists = ubigeoRepository.getDistritos(id)

        mutableState.update {
            it.copy(
                idProvincia = id,
                distritos = dists,
                provinciaError = null,
                idDistrito = null,
                sedes = emptyList(),
                sedeError = null,
                idSedeSeleccionada = null
            )
        }
        loadSedes(currentDepartamento, id)
    }

    fun onDistritoSelected(id: String) {
        mutableState.update { it.copy(idDistrito = id, distritoError = null) }
    }

    fun onSedeSelected(nombre: String) {
        val sede = state.value.sedes.find { it.nombreSede == nombre }
        updateState {
            copy(
                idSedeSeleccionada = sede?.sedeUid,
                sedeError = null
            )
        }
    }

    private fun RegisterDatosState.toRequestDto() = RegisterUsuarioRequestDto(
        usuarioUid = usuarioUid ?: personaUid.orEmpty(),
        nombres = nombres.orEmpty(),
        apellidoPaterno = apellidoPaterno.orEmpty(),
        apellidoMaterno = apellidoMaterno.orEmpty(),
        correo = correo.orEmpty(),
        genero = genero.orEmpty(),
        celular = celular.orEmpty(),
        fechaNacimiento = fechaNacimiento,
        departamento = idDepartamento.orEmpty(),
        provincia = idProvincia.orEmpty(),
        distrito = idDistrito.orEmpty(),
        horarios = horarios.map { it.toDTO() },
        sedePreferencia = idSedeSeleccionada.orEmpty()
    )

    fun onRegisterPersona() {
        val s = state.value
        val validation = validator.validate(s, isLogged)

        if (validation.hasErrors) {
            updateState {
                copy(
                    nombresError = validation.getError("nombres"),
                    apellidoPaternoError = validation.getError("apellidoPaterno"),
                    apellidoMaternoError = validation.getError("apellidoMaterno"),
                    generoError = validation.getError("genero"),
                    fechaNacimientoError = validation.getError("fechaNacimiento"),
                    celularError = validation.getError("celular"),
                    correoError = validation.getError("correo"),
                    departamentoError = validation.getError("departamento"),
                    provinciaError = validation.getError("provincia"),
                    distritoError = validation.getError("distrito"),
                    sedeError = validation.getError("sede"),
                    horarioError = validation.getError("horario"),
                    errorMessage = null
                )
            }
            return
        }

        screenModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null, successMessage = null) }

            registerPersonaUseCase(s.toRequestDto(),isLogged)
                .onSuccess {
                    updateState {
                        copy(
                            isLoading = false,
                            successMessage = it
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        mutableState.update { it.copy(successMessage = null) }
    }

    fun onAllDayToggle(enabled: Boolean) {
        mutableState.update { currentState ->
            val newHorarios = currentState.horarios.map { dia ->
                if (enabled) {
                    dia.copy(habilitado = true, horaInicio = "00:00", horaFin = "23:59")
                } else {
                    dia.copy(habilitado = false)
                }
            }
            currentState.copy(horarios = newHorarios, horarioError = null)
        }
    }

    fun setAllDay(index: Int, isFullDay: Boolean) {
        mutableState.update { state ->
            val nuevaLista = state.horarios.toMutableList()
            val diaActual = nuevaLista[index]

            nuevaLista[index] = if (isFullDay) {
                diaActual.copy(habilitado = true, horaInicio = "00:00", horaFin = "23:59")
            } else {
                diaActual.copy(horaInicio = "08:00", horaFin = "22:00")
            }

            state.copy(horarios = nuevaLista, horarioError = null)
        }
    }

    private inline fun updateState(
        crossinline block: RegisterDatosState.() -> RegisterDatosState
    ) {
        mutableState.update { it.block() }
    }
}