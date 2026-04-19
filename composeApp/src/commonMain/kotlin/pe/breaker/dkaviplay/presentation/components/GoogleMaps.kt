package pe.breaker.dkaviplay.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun GoogleMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double
)