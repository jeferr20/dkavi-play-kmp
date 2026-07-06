package pe.breaker.dkaviplay.presentation.screen.rangos

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.rangos.components.LigaHeader
import pe.breaker.dkaviplay.presentation.screen.rangos.components.RangoItem
import pe.breaker.dkaviplay.presentation.screen.rangos.components.ResumenRangoUser
import pe.breaker.dkaviplay.presentation.util.StatusUiType

class RangosScreen : Screen {
    @Composable
    override fun Content() {
        val rangoModel = koinScreenModel<RangoScreenModel>()
        val state by rangoModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            modifier = Modifier.imePadding(),
            containerColor = Color.Black
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ){
                item {
                    CustomAppbar(modifier = Modifier.padding(horizontal = 16.dp),text = "Progreso de Carrera", onClick = { navigator.pop() })
                    ResumenRangoUser(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        puntosTotales = state.puntosUsuario.toString(),
                        puntosParaSubir = state.siguienteRango?.let { (it.puntosMin - state.puntosUsuario).toString() } ?: "0",
                        nextRango = state.siguienteRango?.categoria ?: "MAX",
                        stats = state.progreso
                    )
                }

                val grupos = state.listaRangos.chunked(5)

                grupos.forEachIndexed { index, rangosDeLiga ->
                    val nombreLiga = when(index) {
                        0 -> "LIGA PRINCIPIANTE"
                        1 -> "LIGA INTERMEDIA"
                        2 -> "LIGA AVANZADA"
                        3 -> "LIGA PROFESIONAL"
                        else -> "LIGA SOBERANA"
                    }

                    stickyHeader {
                        LigaHeader(nombreLiga)
                    }

                    items(rangosDeLiga) { rango ->
                        val esActual = rango.categoria == state.rangoActual?.categoria
                        val esSuperado = (rango.puntosMax ?: Int.MAX_VALUE) < state.puntosUsuario

                        RangoItem(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            nombreRango = rango.categoria,
                            puntosRango = if (rango.puntosMax != null) "${rango.puntosMin} - ${rango.puntosMax}" else "+${rango.puntosMin}",
                            isActual = esActual,
                            isLocked = !esActual && !esSuperado, // Nueva propiedad
                            isCompleted = esSuperado // Nueva propiedad
                        )
                    }
                }
            }

            if (state.isLoading) {
                LoadingDialog(
                    message = "Cargando rangos...",
                    subMessage = "Espere por favor"
                )
            }

            state.errorMessage?.let { errorMsg ->
                StatusDialog(
                    status = StatusUiType.ERROR,
                    message = errorMsg,
                    onDismiss = { rangoModel.clearError() }
                )
            }
        }
    }
}