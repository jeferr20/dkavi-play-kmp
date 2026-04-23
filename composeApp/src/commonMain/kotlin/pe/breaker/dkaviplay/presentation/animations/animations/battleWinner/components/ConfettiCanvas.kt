package pe.breaker.dkaviplay.presentation.animations.animations.battleWinner.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import pe.breaker.dkaviplay.domain.animations.ConfettiParticle
import kotlin.random.Random

@Composable
fun ConfettiCanvas(
    show: Boolean,
    modifier: Modifier = Modifier
) {
    if (!show) return

    // Usamos una lista normal para los cálculos y un estado para el dibujado
    var particles by remember { mutableStateOf(listOf<ConfettiParticle>()) }

    LaunchedEffect(show) {
        val colors = listOf(Color.Cyan, Color.Magenta, Color.Yellow, Color(0xFFFFD700), Color.White)
        val currentParticles = mutableListOf<ConfettiParticle>()

        repeat(80) {
            currentParticles.add(
                ConfettiParticle(
                    x = 0.5f,
                    y = 0.7f,
                    vx = (Random.nextFloat() - 0.5f) * 0.03f, // Bajé un poco la velocidad
                    vy = -(Random.nextFloat() * 0.04f + 0.02f),
                    color = colors.random(),
                    size = Random.nextFloat() * 15f + 10f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 15f,
                    isCircle = Random.nextBoolean()
                )
            )
        }

        val gravity = 0.0008f

        while (currentParticles.isNotEmpty()) {
            withFrameNanos { _ ->
                val iterator = currentParticles.iterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    if (p.y > 1.1f) {
                        iterator.remove()
                    } else {
                        // Actualización física manual (más rápido que copy en cada iteración)
                        val index = currentParticles.indexOf(p)
                        currentParticles[index] = p.copy(
                            x = p.x + p.vx,
                            y = p.y + p.vy,
                            vy = p.vy + gravity,
                            rotation = p.rotation + p.rotationSpeed
                        )
                    }
                }
                // Sincronizamos con el estado de Compose una vez por frame
                particles = currentParticles.toList()
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            // Dibujamos usando el estado sincronizado
            rotate(p.rotation, pivot = Offset(p.x * size.width, p.y * size.height)) {
                drawRect(
                    color = p.color,
                    topLeft = Offset((p.x * size.width) - p.size/2, (p.y * size.height) - p.size/2),
                    size = Size(p.size, if (p.isCircle) p.size else p.size * 0.6f) // Variedad de formas
                )
            }
        }
    }
}