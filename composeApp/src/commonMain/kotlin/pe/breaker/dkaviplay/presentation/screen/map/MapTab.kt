package pe.breaker.dkaviplay.presentation.screen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.presentation.component.map.NativeMapView
import pe.breaker.dkaviplay.presentation.screen.map.components.SedeDetailContent
import pe.breaker.dkaviplay.util.ToastHandler

object MapTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 0u,
            title = "Mapa",
            icon = rememberVectorPainter(Icons.Default.Map)
        )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.parent
        val mapModel = koinScreenModel<MapTabModel>()
        val toastHandler = koinInject<ToastHandler>()

        val state by mapModel.state.collectAsState()

        val sheetState = rememberModalBottomSheetState()
        var selectedSede by remember { mutableStateOf<Sede?>(null) }
        var showSheet by remember { mutableStateOf(false) }

        val uriHandler = LocalUriHandler.current

        val centerCoords = remember(state.sedes) {
            val sedes = state.sedes
            if (!sedes.isNullOrEmpty()) {
                val avgLat = sedes.map { it.latitud }.average()
                val avgLng = sedes.map { it.longitud }.average()
                Pair(avgLat, avgLng)
            } else {
                Pair(-8.117074, -79.038190)
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (state.isSuccess && state.sedes != null) {
                Column(Modifier.fillMaxSize()) {
                    NativeMapView(
                        modifier = Modifier.fillMaxSize(),
                        lat = centerCoords.first, // Coordenadas iniciales
                        lng = centerCoords.second,
                        sedes = state.sedes ?: mutableListOf(),
                        onMarkerClick = { sede ->
                            selectedSede = sede
                            showSheet = true
                            mapModel.listenToMesas(sede.sedeUid)
                        }
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White)
            }

            state.errorMessage?.let { error ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: $error", color = Color.Red)
                    Button(onClick = { mapModel.getSedes() }) {
                        Text("Reintentar")
                    }
                }
            }
            selectedSede?.let { sede ->
                if (showSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showSheet = false
                            selectedSede = null
                            mapModel.stopListeningMesas()
                        },
                        sheetState = sheetState,
                        containerColor = Color(0xFF1A1C1E), // Gris oscuro de la imagen
                        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
                    ) {
                        SedeDetailContent(
                            sede,
                            ocupacion = state.ocupacionActual,
                            onReservationClick = {
//                                navigator?.push(RegisterReservationScreen(sede = sede))
                            },
                            onQuickPlayClick = {
                                showSheet = false
//                                navigator?.push(QuickPlayScreen(sede))
                            },
                            onCallClick = { phone ->
                                try {
                                    if (phone.isBlank()) throw Exception("Número no disponible")
                                    uriHandler.openUri("tel:$phone")
                                } catch (e: Exception) {
                                    toastHandler.showToast("No se pudo realizar la llamada: ${e.message}")
                                }
                            },
                            onMessageClick = { phone ->
                                try {
                                    if (phone.isBlank()) throw Exception("Número no disponible")
                                    val cleanPhone = phone.filter { it.isDigit() }
                                    val whatsappUrl = "https://wa.me/$cleanPhone"
                                    uriHandler.openUri(whatsappUrl)
                                } catch (e: Exception) {
                                    toastHandler.showToast("No se pudo abrir WhatsApp: ${e.message}")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}