package pe.breaker.dkaviplay.presentation.screen.registerDatos

import pe.breaker.dkaviplay.domain.model.ValidationResult
import pe.breaker.dkaviplay.util.isMayorEdad
import pe.breaker.dkaviplay.util.isValidEmail
import pe.breaker.dkaviplay.util.isValidPhone

class RegisterDatosValidator {
    fun validate(state: RegisterDatosState, isLogged: Boolean): ValidationResult {
        val errors = mutableMapOf<String, String?>()

        if (state.nombres.isNullOrBlank()) errors["nombres"] = "Nombres obligatorios"
        if (state.apellidoPaterno.isNullOrBlank()) errors["apellidoPaterno"] =
            "Apellido Paterno obligatorio"
        if (state.apellidoMaterno.isNullOrBlank()) errors["apellidoMaterno"] =
            "Apellido Materno obligatorio"
        if (state.genero.isNullOrBlank()) errors["genero"] = "Género obligatorio"
        if (state.idSedeSeleccionada.isNullOrBlank()) errors["sede"] =
            "Sede de Preferencia obligatoria"
        if (state.idDepartamento.isNullOrBlank()) errors["departamento"] =
            "Departamento obligatorio"
        if (state.idProvincia.isNullOrBlank()) errors["provincia"] = "Provincia obligatoria"
        if (state.idDistrito.isNullOrBlank()) errors["distrito"] = "Distrito obligatorio"

        if (state.correo.isNullOrBlank()) {
            errors["correo"] = "Correo obligatorio"
        } else if (!state.correo.isValidEmail()) {
            errors["correo"] = "El formato del correo no es válido"
        }

        if (state.celular.isNullOrBlank()) {
            errors["celular"] = "Celular obligatorio"
        } else if (!state.celular.isValidPhone()) {
            errors["celular"] = "El número debe empezar con 9 y tener 9 dígitos"
        }

        if (!isLogged) {
            if (state.fechaNacimiento.isNullOrBlank()) {
                errors["fechaNacimiento"] = "Fecha de nacimiento obligatoria"
            } else if (!state.fechaNacimiento.isMayorEdad()) {
                errors["fechaNacimiento"] = "Debes ser mayor de 15 años"
            }
        }

        val horariosActivos = state.horarios.filter { it.habilitado }
        if (horariosActivos.isEmpty()) {
            errors["horario"] = "Debes activar al menos un día de atención"
        }else{
            val hayHorarioInvalido = horariosActivos.any { dia ->
                !isValidTimeFormat(dia.horaInicio) ||
                        !isValidTimeFormat(dia.horaFin) ||
                        !isStartBeforeEnd(dia.horaInicio, dia.horaFin)
            }

            if (hayHorarioInvalido) {
                errors["horario"] = "Revisa que las horas sean válidas (Ej: 08:00) y el inicio sea menor al fin"
            }
        }
        return ValidationResult(errors)
    }

    private fun isValidTimeFormat(time: String): Boolean {
        val regex = Regex("^([01][0-9]|2[0-3]):([0-5][0-9])$")
        return regex.matches(time)
    }

    private fun isStartBeforeEnd(start: String, end: String): Boolean {
        return try {
            val startParts = start.split(":").map { it.toInt() }
            val endParts = end.split(":").map { it.toInt() }

            val startMinutes = startParts[0] * 60 + startParts[1]
            val endMinutes = endParts[0] * 60 + endParts[1]

            startMinutes < endMinutes
        } catch (e: Exception) {
            false
        }
    }
}