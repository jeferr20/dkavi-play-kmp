package pe.breaker.dkaviplay.presentation.screen.aceptarReto.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.util.DateTimeFormatter

@Composable
fun DetalleReservaCard(
    reserva: Reserva,
    dateTimeFormatter: DateTimeFormatter = koinInject()
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DetailRow(Icons.Default.LocationOn, "Sede", reserva.sede)
            DetailRow(Icons.Default.Event, "Fecha y Hora", dateTimeFormatter.formatToFullDateTime(reserva.fechaInicio))
            DetailRow(Icons.Default.TableBar, "Mesa Asignada", reserva.mesa.ifEmpty { "Por asignar" })
            DetailRow(Icons.Default.Payments, "Tu parte a pagar", "${reserva.montoTotal / 2} monedas")
        }
    }
}
