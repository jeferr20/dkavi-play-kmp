package pe.breaker.dkaviplay.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.animateToCameraPosition
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectZero
import platform.CoreLocation.CLLocationCoordinate2DMake

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double
) {
    // 1. Creamos la coordenada de iOS
    val coordinate = CLLocationCoordinate2DMake(lat, lng)

    // 2. Creamos y recordamos el objeto GMSMapView (nativo de iOS)
    val mapView = remember {
        GMSMapView.mapWithFrame(
            frame = CGRectZero.readValue(),
            camera = GMSCameraPosition.cameraWithTarget(coordinate, zoom = 15f)
        )
    }

    // 3. Insertamos el componente nativo de UIKit dentro de Compose
    UIKitView(
        factory = {
            // Añadimos un marcador de forma imperativa (estilo iOS)
            val marker = GMSMarker()
            marker.position = coordinate
            marker.title = "Ubicación seleccionada"
            marker.map = mapView

            mapView // Retornamos la vista que queremos mostrar
        },
        modifier = modifier,
        update = { view ->
            // Si lat/lng cambian, actualizamos la cámara aquí
            val newTarget = CLLocationCoordinate2DMake(lat, lng)
            view.animateToCameraPosition(GMSCameraPosition.cameraWithTarget(newTarget, 15f))
        }
    )
}