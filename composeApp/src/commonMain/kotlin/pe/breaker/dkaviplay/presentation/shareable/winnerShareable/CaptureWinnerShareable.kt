package pe.breaker.dkaviplay.presentation.shareable.winnerShareable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.unit.dp
import pe.breaker.dkaviplay.presentation.screen.resultadosPartida.ResultadoPartidaState
import pe.breaker.dkaviplay.presentation.shareable.WinnerShareable


@Composable
fun CaptureWinnerShareable(
    state: ResultadoPartidaState,
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
        WinnerShareable(
            ganadorName = state.ganador?.usuario ?: "",
            perderdorNamer = state.perdedor?.usuario ?: "",
            ganadorRank = state.ganador?.rango ?: "",
            perdedorRank = state.perdedor?.rango ?: "",
            ganadorUrlImage = state.ganador?.imagen,
            perdedorUrlImagen = state.perdedor?.imagen,
            modifier = Modifier.fillMaxSize()
        )
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(300) // más seguro
        val bitmap = graphicsLayer.toImageBitmap()
        onCaptured(bitmap)
    }
}