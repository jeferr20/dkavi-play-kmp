package pe.breaker.dkaviplay.presentation.screen.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.presentation.components.GoogleMapView
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.screen.map.components.SedeDetailContent
import pe.breaker.dkaviplay.presentation.screen.quickPlay.QuickPlayScreen
import pe.breaker.dkaviplay.presentation.util.ToastHandler
import pe.breaker.dkaviplay.util.handleAction

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
        val uriHandler = LocalUriHandler.current

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var selectedSede by remember { mutableStateOf<Sede?>(null) }
        var showSheet by remember { mutableStateOf(false) }

        val scope = rememberCoroutineScope()

        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (state.sedes != null && state.mapCenter != null) {
                GoogleMapView(
                    modifier = Modifier.fillMaxSize(),
                    lat = state.mapCenter!!.first,
                    lng = state.mapCenter!!.second,
                    sedes = state.sedes!!,
                    onMarkerClick = { sede ->
                        selectedSede = sede
                        showSheet = true
                        mapModel.listenToMesas(sede.sedeUid)
                    }
                )

                if(state.sedes!!.isEmpty()){
                    toastHandler.showToast("No hay sedes en tu región")
                }
            }

            if (state.isLoading || (state.isSuccess && state.mapCenter == null)) LoadingDialog()

            state.errorMessage?.let { error ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: $error", color = Color.Red)
                    Button(onClick = { mapModel.retry() }) {
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
                            sede = sede,
                            ocupacion = state.ocupacionActual,
                            onReservationClick = {
//                                navigator?.push(RegisterReservationScreen(sede = sede))
                            },
                            onQuickPlayClick = {
                                scope.launch {
                                    sheetState.hide()
                                    showSheet = false
                                    selectedSede = null

                                    delay(100)

                                    navigator?.push(QuickPlayScreen(sede))
                                }
                            },
                            onCallClick = { phone ->
                                handleAction(uriHandler, toastHandler, "tel:$phone", "No se pudo realizar la llamada")
                            },
                            onMessageClick = { phone ->
                                val whatsappUrl = "https://wa.me/${phone.filter { it.isDigit() }}"
                                handleAction(uriHandler, toastHandler, whatsappUrl, "No se pudo abrir WhatsApp")
                            }
                        )
                    }
                }
            }
        }
    }
}