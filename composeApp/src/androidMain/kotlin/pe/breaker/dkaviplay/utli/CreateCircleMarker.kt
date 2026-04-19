package pe.breaker.dkaviplay.utli


import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun createCircleMarker(
    context: android.content.Context,
    colorInt: Int,
    sizeDp: Float // Ahora recibe el tamaño dinámico
): BitmapDescriptor {
    val density = context.resources.displayMetrics.density
    val sizePx = (sizeDp * density).toInt().coerceAtLeast(1)

    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = android.graphics.Canvas(bitmap)
    val center = sizePx / 2f
    val radius = sizePx / 3f // Ajustamos proporción

    val paint = android.graphics.Paint().apply {
        color = colorInt
        isAntiAlias = true
    }

    val strokePaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 1.5f * density
        isAntiAlias = true
    }

    canvas.drawCircle(center, center, radius, paint)
    canvas.drawCircle(center, center, radius, strokePaint)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}