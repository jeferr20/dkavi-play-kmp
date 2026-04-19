package pe.breaker.dkaviplay.presentation.component.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import pe.breaker.dkaviplay.domain.model.Sede

@Composable
expect fun NativeMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double,
    sedes: List<Sede>,
    onMarkerClick: (Sede) -> Unit
)