package pe.breaker.dkaviplay.presentation.components.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dkaviplay.composeapp.generated.resources.BolaBillar
import dkaviplay.composeapp.generated.resources.Res
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.presentation.animations.animations.getItem.InteractiveBilliardGiftAnimation

@Composable
fun NewItemDialog(
    premio: DetallePremio,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            InteractiveBilliardGiftAnimation(
                prizeName = premio.nombre,
                prizeRes = premio.imagen,
                ballRes = Res.drawable.BolaBillar,
                onContinue = onDismiss,
                onShare = {  }
            )
        }
    }
}