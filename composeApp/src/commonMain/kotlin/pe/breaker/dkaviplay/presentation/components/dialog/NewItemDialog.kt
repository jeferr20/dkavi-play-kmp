package pe.breaker.dkaviplay.presentation.components.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dkaviplay.composeapp.generated.resources.BolaBillar
import dkaviplay.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.domain.model.inventory.DetallePremio
import pe.breaker.dkaviplay.presentation.animations.animations.getItem.InteractiveBilliardGiftAnimation
import pe.breaker.dkaviplay.presentation.shareable.ItemObtainedShareable
import pe.breaker.dkaviplay.presentation.util.ShareHandler

@Composable
fun NewItemDialog(
    userName: String,
    userImageUrl: String?,
    premio: DetallePremio,
    onDismiss: () -> Unit
) {
    val shareHandler = koinInject<ShareHandler>()
    var triggerCapture by remember { mutableStateOf(false) }

    if (triggerCapture) {
        CaptureNewItemShareable(
            userName = userName,
            userUrlImage = userImageUrl,
            itemName = premio.nombre,
            itemId = premio.id,
            onCaptured = { bitmap ->
                shareHandler.shareBitmap(bitmap)
                triggerCapture = false
            }
        )
    }

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
                onShare = {
                    triggerCapture = true
                }
            )
        }
    }
}

@Composable
fun CaptureNewItemShareable(
    userName: String,
    userUrlImage: String?,
    itemName: String?,
    itemId: Int?,
    onCaptured: (ImageBitmap) -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()

    Box(
        modifier = Modifier
            .size(width = 350.dp, height = 550.dp)
            .alpha(0f)
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawContent()
            }
    ) {
        ItemObtainedShareable(
            userName = userName,
            userUrlImage = userUrlImage,
            itemName = itemName,
            itemId = itemId,
            modifier = Modifier.fillMaxSize()
        )
    }

    LaunchedEffect(Unit) {
        delay(300)
        val bitmap = graphicsLayer.toImageBitmap()
        onCaptured(bitmap)
    }
}