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
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.animations.animations.levelUp.LevelUpProAnimation
import pe.breaker.dkaviplay.presentation.shareable.RankUpShareable
import pe.breaker.dkaviplay.presentation.util.RankResourceMapper
import pe.breaker.dkaviplay.presentation.util.ShareHandler

@Composable
fun LevelUpDialog(
    categoriaAnterior: String?,
    categoria: String?,
    userName: String,
    userImageUrl: String,
    onDismiss: () -> Unit
) {
    val shareHandler = koinInject<ShareHandler>()
    var triggerCapture by remember { mutableStateOf(false) }

    if (triggerCapture) {
        CaptureLevelUpShareable(
            userName = userName,
            userUrlImage = userImageUrl,
            nuevoRango = categoria,
            onCaptured = { bitmap ->
                shareHandler.shareBitmap(bitmap)
                triggerCapture = false
            }
        )
    }

    if (categoriaAnterior != null && categoria!= null) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                usePlatformDefaultWidth = false, // ESTO es la clave para el fullscreen
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            // El Box ocupa toda la pantalla del diálogo
            Box(modifier = Modifier.fillMaxSize()) {
                LevelUpProAnimation(
                    previousLevelName = categoriaAnterior, // Idealmente tendrías el previo
                    newLevelName = categoria,
                    userImageUrl = userImageUrl,
                    oldRankRes = RankResourceMapper.getDrawableByRank(categoriaAnterior),
                    newRankRes = RankResourceMapper.getDrawableByRank(categoria),
                    onClose = onDismiss,
                    onShare = {
                        triggerCapture = true
                    }
                )
            }
        }
    }
}


@Composable
fun CaptureLevelUpShareable(
    userName: String,
    userUrlImage: String,
    nuevoRango: String?,
    onCaptured: (ImageBitmap) -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()

    Box(
        modifier = Modifier
            .size(width = 350.dp, height = 550.dp)
            .alpha(0f)
            .drawWithContent {
                // 🔥 renderizamos el contenido en el layer
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                // dibujamos normalmente también
                drawContent()
            }
    ) {
        RankUpShareable(
            userName = userName,
            userUrlImage = userUrlImage,
            nuevoRango = nuevoRango,
            modifier = Modifier.fillMaxSize()
        )
    }

    LaunchedEffect(Unit) {
        delay(300) // más seguro
        val bitmap = graphicsLayer.toImageBitmap()
        onCaptured(bitmap)
    }
}