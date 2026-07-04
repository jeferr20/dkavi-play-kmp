package pe.breaker.dkaviplay.presentation.shareable.shareQRShareable

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

@Composable
fun CaptureShareQRShareable(
    qrText: String,
    user1: String,
    user2: String,
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
    ){
        ShareQRShareable(
            qrText = qrText,
            user1 = user1,
            user2 = user2
        )
    }

    LaunchedEffect(Unit) {
        delay(300) // más seguro
        val bitmap = graphicsLayer.toImageBitmap()
        onCaptured(bitmap)
    }
}