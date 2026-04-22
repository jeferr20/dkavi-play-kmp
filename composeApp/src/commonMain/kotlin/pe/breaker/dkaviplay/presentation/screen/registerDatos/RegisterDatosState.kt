package pe.breaker.dkaviplay.presentation.screen.registerDatos

import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.domain.model.UbigeoItem


data class RegisterDatosState(
    val personaUid: String? = null,
    val nombres: String? = null,
    val apellidoPaterno: String? = null,
    val apellidoMaterno: String? = null,
    val fechaNacimiento: String? = null,
    val celular: String? = null,
    val genero: String? = null,
    val correo: String? = null,
    val sedes: List<Sede> = emptyList(),
    val idSedeSeleccionada: String? = null,

    val nombresError: String? = null,
    val apellidoPaternoError: String? = null,
    val apellidoMaternoError: String? = null,
    val fechaNacimientoError: String? = null,
    val celularError: String? = null,
    val generoError: String? = null,
    val correoError: String? = null,
    val sedeError: String? = null,

    // --- PASO 2: Ubicación (Ubigeo) ---
    val idDepartamento: String? = null,
    val idProvincia: String? = null,
    val idDistrito: String? = null,

    val departamentos: List<UbigeoItem> = emptyList(),
    val provincias: List<UbigeoItem> = emptyList(),
    val distritos: List<UbigeoItem> = emptyList(),

    val departamentoError: String? = null,
    val provinciaError: String? = null,
    val distritoError: String? = null,

    // --- PASO 2: Disponibilidad ---
    val horarios: List<DiaHorarioState> = defaultHorarios,
    val horarioError: String? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage : String?= null
) {
    companion object {
        val defaultHorarios = listOf(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
        ).map { DiaHorarioState(nombre = it, habilitado = false) }
    }
}

data class DiaHorarioState(
    val nombre: String,
    val habilitado: Boolean = false,
    val horaInicio: String = "08:00",
    val horaFin: String = "22:00"
)