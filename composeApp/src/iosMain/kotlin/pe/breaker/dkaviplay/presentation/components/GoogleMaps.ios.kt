package pe.breaker.dkaviplay.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import pe.breaker.dkaviplay.domain.model.Sede
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.MapKit.MKAnnotationView
import platform.MapKit.MKCoordinateRegionMake
import platform.MapKit.MKCoordinateSpanMake
import platform.MapKit.MKMapView
import platform.MapKit.MKMapViewDelegateProtocol
import platform.MapKit.MKMarkerAnnotationView
import platform.MapKit.MKPointAnnotation
import platform.darwin.NSObject

// 1. Clase personalizada para incrustar el objeto Sede dentro del marcador nativo
@OptIn(ExperimentalForeignApi::class)
class SedeAnnotation(val sede: Sede) : MKPointAnnotation() {
    init {
        setCoordinate(CLLocationCoordinate2DMake(sede.latitud, sede.longitud))
        setTitle(sede.nombreSede)
        setSubtitle(sede.direccion)
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GoogleMapView(
    modifier: Modifier,
    lat: Double,
    lng: Double,
    sedes: List<Sede>,
    onMarkerClick: (Sede) -> Unit
) {
    // 2. Delegado para capturar los clicks
    val mapDelegate = remember {
        object : NSObject(), MKMapViewDelegateProtocol {

            // Reemplazo de mapView:didTapMarker:
            override fun mapView(mapView: MKMapView, didSelectAnnotationView: MKAnnotationView) {
                // Recuperamos nuestra anotación personalizada
                val annotation = didSelectAnnotationView.annotation as? SedeAnnotation
                if (annotation != null) {
                    onMarkerClick(annotation.sede)
                }

                // Deseleccionamos inmediatamente para que el usuario pueda volver
                // a tocar el mismo marcador si cierra el bottom sheet
                mapView.deselectAnnotation(annotation, animated = false)
            }

            // Configuración visual del marcador (El equivalente a tu createCircleMarkerIos)
            override fun mapView(mapView: MKMapView, viewForAnnotation: platform.MapKit.MKAnnotationProtocol): MKAnnotationView? {
                // Ignoramos el punto de ubicación del usuario para no sobreescribirlo
                if (viewForAnnotation !is SedeAnnotation) return null

                val identifier = "SedeMarker"
                var annotationView = mapView.dequeueReusableAnnotationViewWithIdentifier(identifier) as? MKMarkerAnnotationView

                if (annotationView == null) {
                    annotationView = MKMarkerAnnotationView(viewForAnnotation, identifier).apply {
                        // Aquí puedes personalizar el marcador nativo
                        canShowCallout = false // Ponlo en true si quieres que salga el globito nativo con el título
                        // markerTintColor = platform.UIKit.UIColor.blueColor // Personalizar color
                    }
                } else {
                    annotationView.annotation = viewForAnnotation
                }
                return annotationView
            }
        }
    }

    val mapView = remember {
        MKMapView().apply {
            delegate = mapDelegate
            showsUserLocation = true
            showsCompass = false
        }
    }

    UIKitView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            // 3. Actualizar Cámara
            // MapKit usa CoordinateRegion y Span en lugar de un nivel de zoom en Float (0.015 equivale aprox a zoom 15)
            val center = CLLocationCoordinate2DMake(lat, lng)
            val span = MKCoordinateSpanMake(0.015, 0.015)
            val region = MKCoordinateRegionMake(center, span)
            view.setRegion(region, animated = true)

            // 4. Limpiar y redibujar marcadores
            // Filtramos para borrar solo nuestras Sedes y no borrar el punto azul de ubicación del usuario
            val existingAnnotations = view.annotations.filterIsInstance<SedeAnnotation>()
            view.removeAnnotations(existingAnnotations)

            val newAnnotations = sedes.map { SedeAnnotation(it) }
            view.addAnnotations(newAnnotations)
        }
    )
}