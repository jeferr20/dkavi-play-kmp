package pe.breaker.dkaviplay.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.di.AppConfigManager
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.model.rango.RangoRegistry
import pe.breaker.dkaviplay.presentation.components.dialog.LevelUpDialog
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.NewItemDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.util.StatusUiType
import pe.breaker.poolstreet.AppConfigGlobal

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

    LaunchedEffect(uiManager.esperandoPremioPorSubida) {
        if (uiManager.esperandoPremioPorSubida) {
            delay(10000)
            uiManager.timeoutEsperandoPremio()
        }
    }

    Box {
        if (appEnabled && !needsUpdate) {
            content()
        }

        if (!appEnabled) {
            StatusDialog(
                status = StatusUiType.ERROR,
                message = "Dkavi Play se encuentra en mantenimiento. Estamos mejorando la mesa para ti.",
                onDismiss = {},
                confirmButtonText = "Entiendo",
                onConfirm = { closeApp() },
                hideCancelar = true
            )
        }

        if (needsUpdate) {
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

        if (uiManager.esperandoPremioPorSubida) LoadingDialog()

        uiManager.levelUpData?.let { rango ->
            val rangoPrevio = remember(rango.nivel) { RangoRegistry.obtenerRangoAnterior(rango.nivel) }
            LevelUpDialog(
                categoriaAnterior = rangoPrevio?.categoria,
                categoria = rango.categoria,
                userImageUrl = sessionManager.getCurrentUsuario()?.urlImagen ?: "",
                onDismiss = { uiManager.handleLevelUpDismiss() }
            )
        }

        uiManager.premioGanadoGeneral?.let { premio ->
            NewItemDialog(
                premio = premio,
                onDismiss = { uiManager.dismissPremio() }
            )
        }
    }
}