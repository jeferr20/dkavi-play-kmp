package pe.breaker.dkaviplay.presentation.screen.quickPlay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.di.PlatformContext
import pe.breaker.dkaviplay.domain.model.Sede
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.components.EditText
import pe.breaker.dkaviplay.presentation.components.button.ActionIconButton
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.screen.quickPlay.component.ItemPlayer
import pe.breaker.dkaviplay.presentation.screen.quickPlayDetail.QuickPlayDetailScreen
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.util.InputType
import pe.breaker.dkaviplay.presentation.component.EmptyStateMessage
import pe.breaker.dkaviplay.presentation.component.ErrorMessage
import pe.breaker.dkaviplay.presentation.util.lectorQR.LectorQR

class QuickPlayScreen(val sede: Sede) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val quickPlayModel = koinScreenModel<QuickPlayModel>()
        val state by quickPlayModel.state.collectAsState()
        val platformContext = koinInject<PlatformContext>()

        var searchQuery by remember { mutableStateOf("") }
        var showScanner by remember { mutableStateOf(false) }

        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.imePadding()
        ) { innerPadding ->
            if (showScanner) {
                Box(modifier = Modifier.fillMaxSize().zIndex(1f)) {
                    LectorQR(
                        modifier = Modifier.padding(innerPadding),
                        context = platformContext.androidContext,
                        onQrDetected = { qrCode ->
                            if (showScanner) {
                                showScanner = false
                                searchQuery = qrCode
                                quickPlayModel.searchUser(qrCode)
                            }
                        }
                    )
                    CustomAppbar(
                        modifier = Modifier.align(Alignment.TopStart).padding(innerPadding).padding(horizontal = 16.dp),
                        onClick = {showScanner = false}
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                CustomAppbar(text = "Partida Rápida", onClick = { navigator.pop() })

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditText(
                        modifier = Modifier.weight(1f),
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (state.errorMessage != null) quickPlayModel.clearError()
                        },
                        placeholder = "Buscar Usuario",
                        icon = Icons.Default.Search,
                        inputType = InputType.TEXTO
                    )

                    ActionIconButton(
                        icon = Icons.Default.QrCode,
                        contentDescription = "QR",
                        onClick = { showScanner = true },
                        colorBackground = colorPrimary,
                        enabled = !state.isLoading,
                        colorIcon = Color.White,
                    )

                    ActionIconButton(
                        icon = Icons.Default.Search,
                        contentDescription = "Search",
                        onClick = { quickPlayModel.searchUser(searchQuery) },
                        colorBackground = colorPrimary,
                        enabled = !state.isLoading,
                        colorIcon = Color.White,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when {
                        state.errorMessage != null -> {
                            ErrorMessage(message = state.errorMessage!!)
                        }

                        state.isSuccess && state.usersFound.isEmpty() -> {
                            EmptyStateMessage("No se encontraron usuarios disponibles")
                        }

                        state.usersFound.isNotEmpty() -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(state.usersFound) { player ->
                                    ItemPlayer(
                                        modifier = Modifier.fillMaxWidth(),
                                        user = player,
                                        onChallengeClick = {
                                            navigator.push(QuickPlayDetailScreen(sede,it))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                LoadingDialog(
                    message = "Buscando Jugadores...",
                    subMessage = "Espere por favor"
                )
            }
        }
    }
}