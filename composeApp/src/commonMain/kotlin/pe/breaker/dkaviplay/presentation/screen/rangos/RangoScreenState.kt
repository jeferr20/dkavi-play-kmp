package pe.breaker.dkaviplay.presentation.screen.rangos

import pe.breaker.dkaviplay.core.domain.model.rango.Rango

data class RangoScreenState(
    val listaRangos: List<Rango> = emptyList(),
    val rangoActual: Rango? = null,
    val siguienteRango: Rango? = null,
    val progreso: Float = 0f,
    val puntosUsuario: Int = 0,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)