package pe.breaker.dkaviplay.presentation.screen.rangos

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.core.domain.model.rango.Rango
import pe.breaker.dkaviplay.core.domain.model.rango.RangoRegistry

class RangoScreenModel(
    private val sessionManager: UserSessionManager,
) : StateScreenModel<RangoScreenState>(RangoScreenState()) {

    init{
        cargarDatos()
    }

    private fun cargarDatos() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }

            sessionManager.getCurrentUsuarioFlow().collect{ usuario ->
                if(usuario!=null){
                    val puntosActuales = usuario.puntos.toInt()

                    val actual = RangoRegistry.obtenerRangoPorPuntos(puntosActuales)

                    val siguienteRangoRaw = RangoRegistry.obtenerSiguienteRango(actual.nivel)

                    val siguienteRango = siguienteRangoRaw?.let {
                        RangoRegistry.obtenerRangoPorPuntos(it.puntosMin)
                    }

                    val prog = calcularProgreso(puntosActuales, actual, siguienteRango)

                    mutableState.update {
                        it.copy(
                            isLoading = false,
                            listaRangos = RangoRegistry.obtenerTodosLosRangos(),
                            rangoActual = actual,
                            siguienteRango = siguienteRango,
                            progreso = prog,
                            puntosUsuario = puntosActuales
                        )
                    }
                }
                else {
                    mutableState.update {
                        it.copy(isLoading = false, errorMessage = "No se encontró el usuario")
                    }
                }
            }
        }
    }

    fun setError(msj: String){
        mutableState.update { it.copy(errorMessage = msj, isLoading = false) }
    }

    fun clearError() {
        mutableState.update { it.copy(errorMessage = null) }
    }

    private fun calcularProgreso(puntos: Int, actual: Rango, siguiente: Rango?): Float {
        if (siguiente == null) return 1.0f

        val rangoMin = actual.puntosMin
        val rangoMax = siguiente.puntosMin

        val base = (rangoMax - rangoMin).toFloat()
        if (base <= 0) return 0f

        return ((puntos - rangoMin).toFloat() / base).coerceIn(0f, 1f)
    }
}