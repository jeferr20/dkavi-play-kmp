package pe.breaker.dkaviplay.presentation.screen.login.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dkaviplay.composeapp.generated.resources.Logo_Billar
import dkaviplay.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
fun IconHeader(
    modifier: Modifier = Modifier,
    floatAnim: Float
) {
    // BoxWithConstraints nos da el maxWidth y maxHeight asignados
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val height = maxHeight

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .align(Alignment.TopCenter)
                .blur(height * 0.15f)
        ) {
            val canvasWidth = size.width
            val lightRadius = canvasWidth * 0.8f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorPrimary.copy(alpha = 0.75f),
                        colorPrimary.copy(alpha = 0.5f),
                        colorPrimary.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(canvasWidth / 2, 0f),
                    radius = lightRadius
                ),
                radius = lightRadius,
                center = Offset(canvasWidth / 2, 0f)
            )

            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    0.7f to Color.Transparent,
                    1.0f to Color.Black
                ),
                size = size
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val logoSize = height * 0.45f

            Image(
                painter = painterResource(Res.drawable.Logo_Billar),
                contentDescription = "DKAVI Logo",
                modifier = Modifier
                    .size(logoSize)
                    .graphicsLayer { translationY = floatAnim },
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(height * 0.05f))

            Text(
                text = "DKAVI PLAY",
                style = TextStyle(
                    brush = Brush.verticalGradient(listOf(Color.White, Color.LightGray)),
                    fontSize = (height.value * 0.1f).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (height.value * 0.015f).sp
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "LoadingDialog")
@Composable
fun showCarga() {
    val infiniteTransition = rememberInfiniteTransition()
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(modifier = Modifier.fillMaxSize().background(
        Color.Black
    )) {
        IconHeader(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            floatAnim =  floatAnim
        )
    }
}