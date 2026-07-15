package pe.breaker.dkaviplay.presentation.screen.reservas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.domain.model.inventory.TipoPremio
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.aceptarReto.AceptarRetoScreen
import pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo.AcuerdoMutuoScreen
import pe.breaker.dkaviplay.presentation.screen.inventario.InventarioModel
import pe.breaker.dkaviplay.presentation.screen.reservas.components.FilterSection
import pe.breaker.dkaviplay.presentation.screen.reservas.components.InventarioBottomSheetWrapper
import pe.breaker.dkaviplay.presentation.screen.reservas.components.ReservasContent
import pe.breaker.dkaviplay.presentation.screen.resultadosPartida.ResultadoPartidaScreen
import pe.breaker.dkaviplay.presentation.screen.retoIniciado.RetoIniciadoScreen
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.StatusUiType

object ReservasTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 1u,
            title = "Reservas",
            icon = rememberVectorPainter(Icons.Default.Event)
        )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.parent
        val reservaTabModel = koinScreenModel<ReservasModel>()
        val state by reservaTabModel.state.collectAsState()
        val scope = rememberCoroutineScope()

        val inventarioModel = koinScreenModel<InventarioModel>(
            parameters = { parametersOf(TipoPremio.ITEM_JUGABLE) }
        )
        val inventarioState by inventarioModel.state.collectAsState()

        var showInventario by remember { mutableStateOf(false) }
        var reservaSeleccionada by remember { mutableStateOf<Reserva?>(null) }
        val pagerState = rememberPagerState(pageCount = { ReservaFilter.entries.size })

        val errorMessage = state.errorMessage
        val isInitialLoading = state.isLoading && state.reservas.isEmpty()

        LaunchedEffect(pagerState.currentPage) {
            val targetFilter = ReservaFilter.entries[pagerState.currentPage]
            if (state.currentFilter != targetFilter) {
                reservaTabModel.applyFilter(targetFilter)
            }
        }

        LaunchedEffect(state.currentFilter) {
            val targetPage = ReservaFilter.entries.indexOf(state.currentFilter)
            if (pagerState.currentPage != targetPage) {
                pagerState.animateScrollToPage(targetPage)
            }
        }

        val pullToRefreshState = rememberPullToRefreshState()
        val isRefreshing = state.isLoading && state.reservas.isNotEmpty()

        Scaffold(
            containerColor = Color.Black,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                ) {
                    Spacer(Modifier.statusBarsPadding())

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        FilterSection(
                            currentFilter = state.currentFilter,
                            onFilterSelected = { filter ->
                                scope.launch {
                                    val index = ReservaFilter.entries.indexOf(filter)
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                        )
                    }
                }
            }
        ){ padding ->
            PullToRefreshBox(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = { reservaTabModel.refresh() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = colorBlackSurface,
                        color = colorPrimary
                    )
                }
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    pageSpacing = 16.dp,
                    verticalAlignment = Alignment.Top,
                    userScrollEnabled = !isRefreshing
                ) {
                    ReservasContent(
                        state = state,
                        onEliminar = { reserva ->
                            reservaTabModel.eliminarReserva(reserva.reservaUid)
                        },
                        onAceptarRechazarReto = { reserva ->
                            navigator?.push(AceptarRetoScreen(reserva.reservaUid))
                        },
                        onStartGame = { reserva ->
                            if (!state.isLoading) {
                                scope.launch {
                                    if (reservaTabModel.validarHoraInicio(reserva)) {
                                        navigator?.push(RetoIniciadoScreen(reserva.reservaUid))
                                    }
                                }
                            }
                        },
                        onRetry = { reservaTabModel.refresh() },
                        onVerificarResultado = { reserva ->
                            navigator?.push(AcuerdoMutuoScreen(reserva.reservaUid))
                        },
                        onVerResulatdos = { reserva ->
                            navigator?.push(ResultadoPartidaScreen(reserva.reservaUid))
                        },
                        onShowInventario = { reserva ->
                            reservaSeleccionada = reserva
                            showInventario = true
                        }
                    )
                }
            }

            reservaSeleccionada?.let { reserva ->
                if (showInventario) {
                    InventarioBottomSheetWrapper(
                        inventarioModel = inventarioModel,
                        state = inventarioState,
                        reserva = reserva,
                        onDismiss = {
                            showInventario = false
                            reservaSeleccionada = null
                        }
                    )
                }
            }

            if (errorMessage != null) {
                StatusDialog(
                    status = StatusUiType.ERROR,
                    message = errorMessage,
                    confirmButtonText = "Reintentar",
                    onDismiss = { reservaTabModel.clearError() },
                    onConfirm = {
                        reservaTabModel.clearError()
                        reservaTabModel.refresh()
                    }
                )
            }

            if (isInitialLoading) {
                LoadingDialog(message = "Cargando Reservas...", subMessage = "Espere por favor")
            }
        }
    }
}