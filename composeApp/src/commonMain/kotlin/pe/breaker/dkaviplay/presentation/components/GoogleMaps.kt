package pe.breaker.dkaviplay.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.breaker.dkaviplay.core.domain.model.Sede

@Composable
expect fun GoogleMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double,
    sedes: List<Sede>,
    onMarkerClick: (Sede) -> Unit
)