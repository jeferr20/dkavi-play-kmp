package pe.breaker.dkaviplay.presentation.screen.quickPlayDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.domain.model.UserQuick
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.screen.quickPlayDetail.components.CustomLabelInfo
import pe.breaker.dkaviplay.presentation.screen.quickPlayDetail.components.PorcentajeVictoria
import pe.breaker.dkaviplay.presentation.screen.quickPlayDetail.components.ProfileHeader
import pe.breaker.dkaviplay.presentation.screen.registerReserva.RegisterReservationScreen
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.component.ErrorMessage

class QuickPlayDetailScreen(private val sede: Sede, private val usuario: UserQuick) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<QuickPlayDetailModel>()
        val state by model.state.collectAsState()

        LaunchedEffect(usuario) {
            model.getUser(userUid = usuario.userUid!!)
        }

        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.imePadding()
        ) { innerPadding ->
            state.usuario?.let { usuario ->
                val porcentajeVictoria = remember(usuario.partidasGanadas, usuario.partidasJugadas) {
                    if (usuario.partidasJugadas > 0) {
                        usuario.partidasGanadas.toFloat() / usuario.partidasJugadas
                    } else 0f
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    CustomAppbar(onClick = { navigator.pop() })

                    Spacer(modifier = Modifier.height(24.dp))

                    ProfileHeader(
                        imageUrl = usuario.imagen,
                        nombre = usuario.usuario,
                        rango = usuario.rango
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        CustomLabelInfo(
                            modifier = Modifier.weight(1f),
                            label = "Partidas Ganadas",
                            text = usuario.partidasGanadas.toString(),
                            color = colorPrimary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        CustomLabelInfo(
                            modifier = Modifier.weight(1f),
                            label = "Partidas Jugadas", // Corregido el label
                            text = usuario.partidasJugadas.toString(),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    PorcentajeVictoria(
                        modifier = Modifier.fillMaxWidth(),
                        porcentaje = porcentajeVictoria
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    CustomButtonFilled(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = true,
                        onClick = { navigator.push(RegisterReservationScreen(sede , usuario))},
                        text = "Retar a partida"
                    )
                }
            }

            if (state.isLoading) LoadingDialog()

            state.errorMessage?.let { ErrorMessage(message = it) }
        }
    }
}