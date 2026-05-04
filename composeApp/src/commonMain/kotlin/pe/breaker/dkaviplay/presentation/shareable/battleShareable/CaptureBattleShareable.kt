package pe.breaker.dkaviplay.presentation.shareable.battleShareable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import pe.breaker.dkaviplay.presentation.screen.retoIniciado.RetoIniciadoState


@Composable
fun CaptureBattleShareable(
    state: RetoIniciadoState,
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
        BattleShareable(
            user1Name = state.retador?.usuario ?: "",
            user2Name = state.retado?.usuario ?: "",
            user1Rank = state.retador?.rango ?: "",
            user2Rank = state.retado?.rango ?: "",
            user1UrlImage = state.retador?.imagen ?: "",
            user2UrlImage = state.retado?.imagen ?: ""
        )
    }

    LaunchedEffect(Unit) {
        delay(300) // más seguro
        val bitmap = graphicsLayer.toImageBitmap()
        onCaptured(bitmap)
    }
}