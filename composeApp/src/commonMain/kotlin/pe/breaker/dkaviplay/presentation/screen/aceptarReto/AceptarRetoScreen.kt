package pe.breaker.dkaviplay.presentation.screen.aceptarReto

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.screen.aceptarReto.components.RetoContent
import pe.breaker.dkaviplay.presentation.screen.mainContainer.MainContainerScreen

class AceptarRetoScreen(val reservaId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<AceptarRetoModel>(
            parameters = { parametersOf(reservaId) }
        )
        val state by screenModel.state.collectAsState()

        LaunchedEffect(state.shouldRedirectToMain) {
            if (state.shouldRedirectToMain) {
                if (navigator.size > 1) {
                    navigator.pop()
                } else {
                    navigator.replaceAll(MainContainerScreen())
                }
            }
        }

        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.imePadding()
        ){ innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ){
                CustomAppbar(onClick = { navigator.pop() })

                Spacer(modifier = Modifier.height(24.dp))

                when{
                    state.isLoading -> LoadingDialog()

                    state.errorMessage != null -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Oops!", style = MaterialTheme.typography.headlineLarge)
                            Text(state.errorMessage!!, textAlign = TextAlign.Center)
                        }
                    }

                    state.reserva != null && state.retador != null ->{
                        RetoContent(
                            reserva = state.reserva!!,
                            retador = state.retador!!,
                            successMessage = state.successMessage,
                            onAccept = { screenModel.responderReto(true) },
                            onReject = { screenModel.responderReto(false) },
                            onClose = { navigator.pop() }
                        )
                    }
                }
            }
        }
    }
}