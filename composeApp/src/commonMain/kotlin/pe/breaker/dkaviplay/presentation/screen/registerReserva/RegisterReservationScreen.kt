package pe.breaker.dkaviplay.presentation.screen.registerReserva

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dkaviplay.composeapp.generated.resources.Res
import dkaviplay.composeapp.generated.resources.billas
import dkaviplay.composeapp.generated.resources.pool
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.domain.model.TipoJuego
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.datetime.DatePickerModal
import pe.breaker.dkaviplay.presentation.components.datetime.TimePickerModal
import pe.breaker.dkaviplay.presentation.navigation.GlobalNavigationBus
import pe.breaker.dkaviplay.presentation.navigation.NavigationEvent
import pe.breaker.dkaviplay.presentation.screen.registerReserva.components.GameSelect
import pe.breaker.dkaviplay.presentation.screen.registerReserva.components.MesasSection
import pe.breaker.dkaviplay.presentation.screen.registerReserva.components.ReservationStatusOverlay
import pe.breaker.dkaviplay.presentation.screen.registerReserva.components.ReservationTimeSection
import pe.breaker.dkaviplay.presentation.screen.registerReserva.components.ResumenPrecioReserva
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

class RegisterReservationScreen(val sede: Sede, private val usuario: UserQuick) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel =
            koinScreenModel<RegisterReservationScreenModel> { parametersOf(sede.sedeUid) }
        val state by screenModel.state.collectAsState()

        // Estados de UI locales
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        var pickingForStart by remember { mutableStateOf(true) }

        val hasError = state.errorMessage != null
        val isFormComplete = if (state.isImmediate) {
            state.fInicio != null && state.hInicio != null
        } else {
            state.fInicio != null && state.hInicio != null && state.fSalida != null && state.hSalida != null
        }

        LaunchedEffect(usuario.userUid) {
            screenModel.onUsuarioRetadoSelected(usuario)
        }

        // --- Modales de Selección ---
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { screenModel.onDateSelected(it, pickingForStart) },
                onDismiss = { showDatePicker = false }
            )
        }

        if (showTimePicker) {
            TimePickerModal(
                onTimeSelected = { h, m -> screenModel.onTimeSelected(h, m, pickingForStart) },
                onDismiss = { showTimePicker = false }
            )
        }

        // --- Estructura Principal ---
        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.imePadding()
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    CustomAppbar(text = "Reservar Mesa", onClick = { navigator.pop() })

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                    ) {
                        MesasSection(
                            mesas = state.mesas,
                            selectedMesa = state.selectedMesa,
                            onMesaSelected = { screenModel.onMesaSelected(it) },
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { screenModel.onImmediateToggled(!state.isImmediate) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.isImmediate,
                                onCheckedChange = { checked -> screenModel.onImmediateToggled(checked) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colorPrimary,
                                    uncheckedColor = Color.Gray,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reto Inmediato (Comenzar ahora)",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección: Hora Inicio
                        ReservationTimeSection(
                            title = "Hora de Inicio",
                            fechaText = state.fInicio ?: "Fecha",
                            horaText = state.hInicio ?: "Hora",
                            onDateClick = {
                                if (!state.isImmediate) {
                                    pickingForStart = true
                                    showDatePicker = true
                                }
                            },
                            onTimeClick = {
                                if (!state.isImmediate) {
                                    pickingForStart = true
                                    showTimePicker = true
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Sección: Hora Salida
                        ReservationTimeSection(
                            title = "Hora de Salida (Opcional en Reto Inmediato)",
                            fechaText = state.fSalida ?: "Fecha",
                            horaText = state.hSalida ?: "Hora",
                            onDateClick = {
                                if (!state.isImmediate) {
                                    pickingForStart = false
                                    showDatePicker = true
                                }
                            },
                            onTimeClick = {
                                if (!state.isImmediate) {
                                    pickingForStart = false
                                    showTimePicker = true
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "Selecciona el tipo de juego", color = Color.White, style = MaterialTheme.typography.titleMedium)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                        ){
                            GameSelect(
                                modifier = Modifier.weight(1f),
                                imagen = Res.drawable.billas,
                                titulo = TipoJuego.BILLAR.nombre,
                                juegos = TipoJuego.BILLAR.setsMaximos,
                                selected = state.tipoJuego == TipoJuego.BILLAR,
                                onClick = { screenModel.onTipoJuegoSelected(TipoJuego.BILLAR) }
                            )

                            Spacer(modifier = Modifier.width(20.dp))

                            GameSelect(
                                modifier = Modifier.weight(1f),
                                imagen = Res.drawable.pool,
                                titulo = TipoJuego.POOL.nombre,
                                juegos = TipoJuego.POOL.setsMaximos,
                                selected = state.tipoJuego == TipoJuego.POOL,
                                onClick = { screenModel.onTipoJuegoSelected(TipoJuego.POOL) }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        ResumenPrecioReserva(
                            duracion = screenModel.calcularDuracion(),
                            montoReserva = state.tarifario ?: 0.0,
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    CustomButtonFilled(
                        enabled = isFormComplete && !hasError && !state.isLoading,
                        onClick = { screenModel.onSaveReserva() },
                        text = "CONFIRMAR RESERVACIÓN"
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                ReservationStatusOverlay(
                    state = state,
                    onClearError = { screenModel.clearError() },
                    onSuccess = {
                        screenModel.clearSuccess()
                        GlobalNavigationBus.emit(NavigationEvent.GoToReservations)
                        navigator.popUntilRoot()
                    }
                )
            }
        }
    }
}