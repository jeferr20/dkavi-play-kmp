package pe.breaker.dkaviplay.presentation.animations.animations.battleChallenge.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.animations.ChallengeSpark
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ChallengeLightningEffect(spark: ChallengeSpark) {
    val duration = 650
    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            animX.animateTo(
                (cos(spark.angle) * spark.speed).toFloat(),
                tween(duration, easing = LinearOutSlowInEasing)
            )
        }
        launch {
            animY.animateTo(
                (sin(spark.angle) * spark.speed).toFloat(),
                tween(duration, easing = LinearOutSlowInEasing)
            )
        }
        launch { alpha.animateTo(0f, tween(duration)) }
    }

    Box(
        modifier = Modifier
            .offset(x = animX.value.dp, y = animY.value.dp)
            .graphicsLayer {
                rotationZ = (spark.angle * 180 / PI).toFloat() + 90f
                this.alpha = alpha.value
            }
            .size(width = 3.dp, height = 70.dp)
            .background(
                Brush.verticalGradient(listOf(spark.color, Color.Transparent)),
                RoundedCornerShape(50)
            )
    )
}