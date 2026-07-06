package pe.breaker.dkaviplay.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pe.breaker.dkaviplay.core.domain.model.Sede
import pe.breaker.dkaviplay.presentation.theme.colorPrimary

@Composable
actual fun GoogleMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double,
    sedes: List<Sede>,
    onMarkerClick: (Sede) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        MapsInitializer.initialize(context)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 15f)
    }

    LaunchedEffect(lat, lng) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f),
            durationMs = 1000
        )
    }

    val uiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = true,
            compassEnabled = false
        )
    }

    val currentZoom = cameraPositionState.position.zoom
    val dynamicSize = remember(currentZoom) {
        (currentZoom * 2).coerceIn(8f, 32f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = uiSettings
    ){
        sedes.forEach { sede ->
            val markerIcon = remember(sede.sedeUid, dynamicSize) {
                createCircleMarker(context, colorPrimary.toArgb(), dynamicSize)
            }
            Marker(
                state = MarkerState(position = LatLng(sede.latitud, sede.longitud)),
                title = sede.nombreSede,
                icon = markerIcon,
                anchor = Offset(0.5f, 0.5f),
                snippet = sede.direccion,
                onClick = {
                    onMarkerClick(sede)
                    true
                }
            )
        }
    }
}