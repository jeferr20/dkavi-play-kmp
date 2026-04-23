package pe.breaker.dkaviplay.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSize
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIGraphicsBeginImageContextWithOptions
import platform.UIKit.UIGraphicsEndImageContext
import platform.UIKit.UIGraphicsGetImageFromCurrentImageContext
import platform.UIKit.UIImage

@OptIn(ExperimentalForeignApi::class)
fun createCircleMarkerIos(color: UIColor, size: Double): UIImage {
    // 1. Definimos el tamaño usando un CGSize
    val cSize = cValue<CGSize> {
        width = size
        height = size
    }

    // 2. Iniciamos el contexto de imagen con el tamaño correcto
    UIGraphicsBeginImageContextWithOptions(cSize, false, 0.0)

    // El contexto se obtiene automáticamente por los métodos de dibujo de UIKit
    // val context = UIGraphicsGetCurrentContext()

    // 3. Creamos el área de dibujo (CGRect)
    val rect = CGRectMake(0.0, 0.0, size, size)

    // 4. Dibujar el círculo relleno
    val circlePath = UIBezierPath.bezierPathWithOvalInRect(rect)
    color.setFill()
    circlePath.fill()

    // 5. Dibujar el borde blanco (Stroke)
    UIColor.whiteColor.setStroke()
    circlePath.lineWidth = 2.0 // Grosor del borde
    circlePath.stroke()

    // 6. Extraer la imagen y cerrar el contexto
    val image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    return image ?: UIImage()
}

// Helper para convertir tus colores de Compose a UIColor
fun colorFromCompose(color: Color): UIColor {
    val argb = color.toArgb() // Convierte el Color de Compose a un Int (ARGB)

    // Extraemos los componentes usando corrimientos de bits
    val a = ((argb shr 24) and 0xff).toDouble() / 255.0
    val r = ((argb shr 16) and 0xff).toDouble() / 255.0
    val g = ((argb shr 8) and 0xff).toDouble() / 255.0
    val b = (argb and 0xff).toDouble() / 255.0

    return UIColor.colorWithRed(r, g, b, alpha = a)
}