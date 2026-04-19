package pe.breaker.dkaviplay.presentation.component.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.animateToCameraPosition
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import pe.breaker.dkaviplay.domain.model.Sede
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NativeMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double,
    sedes: List<Sede>,
    onMarkerClick: (Sede) -> Unit
) {
    val camera = remember(lat, lng) {
        GMSCameraPosition.cameraWithLatitude(lat, lng, 15f)
    }
    UIKitView(
        modifier = modifier,
        factory = {
            val mapView = GMSMapView.mapWithFrame(platform.CoreGraphics.CGRectZero.readValue(), camera)

            // Configuración de UI
            mapView.settings.myLocationButton = true
            mapView.myLocationEnabled = true

            // Dibujar marcadores
            sedes.forEach { sede ->
                GMSMarker().apply {
                    position = CLLocationCoordinate2DMake(sede.latitud, sede.longitud)
                    title = sede.nombreSede
                    snippet = sede.direccion
                    map = mapView
                    userData = sede // Guardamos el objeto Sede para el click
                }
            }

            // Listener de clicks
            mapView.delegate = object : NSObject(), GMSMapViewDelegateProtocol {
                override fun mapView(mapView: GMSMapView, didTapMarker: GMSMarker): Boolean {
                    val sede = didTapMarker.userData as? Sede
                    if (sede != null) onMarkerClick(sede)
                    return true
                }
            }

            mapView
        },
        update = { mapView ->
            // Si cambian lat/lng desde afuera, el mapa se mueve
            mapView.animateToCameraPosition(GMSCameraPosition.cameraWithLatitude(lat, lng, 15f))
        }
    )
}