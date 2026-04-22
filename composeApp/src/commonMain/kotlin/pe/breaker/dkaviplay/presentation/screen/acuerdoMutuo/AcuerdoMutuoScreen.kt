package pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.arbitro.components.ScoreInputSection
import pe.breaker.dkaviplay.presentation.util.StatusUiType

class AcuerdoMutuoScreen(private val reservaUid: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<AcuerdoMutuoModel>(
            parameters = { parametersOf(reservaUid) }
        )
        val state by screenModel.state.collectAsState()

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

                state.reserva?.let{
                    ScoreInputSection(
                        reserva = it,
                        resultados = state.resultados,
                        onValueChange = { setIndex, ganadorIndex ->
                            screenModel.onActualizarResultado(setIndex, ganadorIndex)
                        },
                        onSend = {
                            screenModel.enviarResultados()
                        }
                    )
                }
            }

            if (state.isLoading && state.errorMessage == null) LoadingDialog()

            if (state.errorMessage != null) {
                StatusDialog(
                    status = StatusUiType.ERROR,
                    message = state.errorMessage?: "Ocurrió un error inesperado",
                    confirmButtonText = "Reintentar",
                    onDismiss = { screenModel.resetError()},
                    onConfirm = { screenModel.resetError()}
                )
            }

            if (state.isSuccess) {
                StatusDialog(
                    status = StatusUiType.SUCCESS,
                    message = state.successMessage ?: "Resultado registrado correctamente",
                    confirmButtonText = "Finalizar",
                    onDismiss = { navigator.popUntilRoot() },
                    onConfirm = { navigator.popUntilRoot() }
                )
            }
        }
    }
}