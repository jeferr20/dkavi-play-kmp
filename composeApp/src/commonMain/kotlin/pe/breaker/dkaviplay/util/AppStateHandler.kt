package pe.breaker.dkaviplay.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.AppConfigGlobal
import pe.breaker.dkaviplay.di.AppConfigManager
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.core.domain.model.rango.RangoRegistry
import pe.breaker.dkaviplay.presentation.components.dialog.LevelUpDialog
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.NewItemDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.util.StatusUiType

@Composable
fun AppStateHandler(
    appConfigManager: AppConfigManager,
    sessionManager: UserSessionManager,
    uiManager: GlobalUiManager = koinInject(),
    content: @Composable () -> Unit
) {
    val config by appConfigManager.config.collectAsState()

    val appEnabled = config.activeServiceMovil
    val currentVersion = AppConfigGlobal.VERSION_NAME
    val remoteVersion = config.versionMovil
    val needsUpdate = isVersionOlder(currentVersion, remoteVersion)

    val uiState = uiManager.uiState

    Box(Modifier.fillMaxSize()) {
        when{
            !appEnabled ->{
                StatusDialog(
                    status = StatusUiType.ERROR,
                    message = "Dkavi Play se encuentra en mantenimiento. Estamos mejorando la mesa para ti.",
                    onDismiss = {},
                    confirmButtonText = "Entiendo",
                    onConfirm = { closeApp() },
                    hideCancelar = true
                )
            }

            needsUpdate ->{
                StatusDialog(
                    status = StatusUiType.WARNING,
                    message = "¡Nueva versión disponible! Es necesario actualizar para seguir usando Dkavi Play.",
                    onDismiss = {},
                    confirmButtonText = "Actualizar",
                    onConfirm = {
                        val url = if (getPlatform().name.contains("Android")) {
                            config.urlMovilAndroid
                        } else {
                            config.urlMovilIOS
                        }
                        openUrl(url)
                    },
                    hideCancelar = true
                )
            }

            else -> { content()}
        }
        when(uiState){
            is UiFlowState.ShowingLevelUp -> {
                val rango = uiState.rango
                val previo = RangoRegistry.obtenerRangoAnterior(rango.nivel)

                LevelUpDialog(
                    userName = sessionManager.getCurrentUsuario()?.usuario ?: "",
                    categoriaAnterior = previo?.categoria,
                    categoria = rango.categoria,
                    userImageUrl = sessionManager.getCurrentUsuario()?.urlImagen ?: "",
                    onDismiss = { uiManager.onLevelUpDismiss() }
                )
            }

            is UiFlowState.ShowingReward -> {
                NewItemDialog(
                    userName = sessionManager.getCurrentUsuario()?.usuario ?: "",
                    userImageUrl = sessionManager.getCurrentUsuario()?.urlImagen ?: "",
                    premio = uiState.premio,
                    onDismiss = { uiManager.dismissReward() }
                )
            }

            is UiFlowState.WaitingReward -> {
                LoadingDialog()
            }

            else -> Unit
        }
    }
}