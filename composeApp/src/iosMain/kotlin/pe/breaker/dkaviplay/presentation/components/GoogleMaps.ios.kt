package pe.breaker.dkaviplay.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMapViewDelegateProtocol
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.animateToCameraPosition
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import pe.breaker.dkaviplay.core.domain.model.Sede
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import platform.CoreGraphics.CGRectZero
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.darwin.NSObject
import kotlin.math.abs

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double,
    sedes: List<Sede>,
    onMarkerClick: (Sede) -> Unit
) {
    val coordinate = CLLocationCoordinate2DMake(lat, lng)
    var lastZoom by remember { mutableStateOf(0f) }

    // 1. Creamos el delegado para capturar clicks
    val mapDelegate = remember {
        object : NSObject(), GMSMapViewDelegateProtocol {
            override fun mapView(mapView: GMSMapView, didTapMarker: GMSMarker): Boolean {
                // Recuperamos la sede guardada en el userData del marcador
                val sede = didTapMarker.userData as? Sede
                if (sede != null) {
                    onMarkerClick(sede)
                }
                return true
            }
        }
    }

    val mapView = remember {
        GMSMapView.mapWithFrame(
            frame = CGRectZero.readValue(),
            camera = GMSCameraPosition.cameraWithTarget(coordinate, zoom = 15f)
        ).apply {
            delegate = mapDelegate // Asignamos el delegado
            settings.myLocationButton = true
            settings.compassButton = false
        }
    }

    UIKitView(
        factory = {
            mapView
        },
        modifier = modifier,
        update = { view ->
            val currentZoom = view.camera.zoom
            if(abs(currentZoom-lastZoom) > 0.5f){
                lastZoom = currentZoom
                val dynamicSize = (currentZoom * 2.0).coerceIn(8.0, 32.0)

                // Actualizar cámara
                val newTarget = CLLocationCoordinate2DMake(lat, lng)
                view.animateToCameraPosition(GMSCameraPosition.cameraWithTarget(newTarget, currentZoom))

                // Limpiar y redibujar marcadores
                view.clear()

                sedes.forEach { sede ->
                    val sedeCoord = CLLocationCoordinate2DMake(sede.latitud, sede.longitud)
                    GMSMarker().apply {
                        position = sedeCoord
                        title = sede.nombreSede
                        snippet = sede.direccion
                        userData = sede // Guardamos el objeto Sede aquí
                        icon = createCircleMarkerIos(
                            color = colorFromCompose(colorPrimary),
                            size = dynamicSize
                        )
                        map = view
                    }
                }
            }
        }
    )
}