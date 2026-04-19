package pe.breaker.dkaviplay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource

import pe.breaker.dkaviplay.presentation.components.GoogleMapView

@Composable
@Preview
fun App() {
    val location = remember { mutableStateOf(Pair(-8.111813, -79.028682)) }
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize().systemBarsPadding()
        ) {
            GoogleMapView(
                modifier = Modifier
                    .fillMaxSize(),
                lat = location.value.first,
                lng = location.value.second
            )
        }
    }
}