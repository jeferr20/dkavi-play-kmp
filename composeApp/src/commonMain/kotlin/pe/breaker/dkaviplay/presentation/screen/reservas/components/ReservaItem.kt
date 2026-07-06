package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import pe.breaker.dkaviplay.core.domain.model.Reserva
import pe.breaker.dkaviplay.core.domain.model.ReservaEstado
import pe.breaker.dkaviplay.presentation.components.button.CustomButtonFilled
import pe.breaker.dkaviplay.presentation.components.button.CustomOutlineButtonTextIcon
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.theme.colorBlanco
import pe.breaker.dkaviplay.presentation.theme.colorCeleste
import pe.breaker.dkaviplay.presentation.theme.colorCelesteNeon
import pe.breaker.dkaviplay.presentation.theme.colorPrimary
import pe.breaker.dkaviplay.presentation.theme.colorRedError
import pe.breaker.dkaviplay.presentation.theme.colorVerdeClaro

@Composable
fun ReservaItem(
    reserva: Reserva,
    currentUsuario: String,
    onEliminar: (Reserva) -> Unit,
    onAceptarRechazarReto: (Reserva) -> Unit,
    onStartGameClick: (Reserva) -> Unit,
    onVerificarResultado: (Reserva) -> Unit,
    onVerResultados: (Reserva) -> Unit,
    onShowInventario: (Reserva) -> Unit,
    onShowQrPago: (Reserva) -> Unit
) {
    val estado = ReservaEstado.fromId(reserva.estadoInt)
    val soyElCreador = reserva.creadorUid == currentUsuario
    val soyElRetado = reserva.retadoUid == currentUsuario
    val soyUsuarioPendienteRpta = reserva.userPendienteUid == currentUsuario
    val bothUsersReady = reserva.userRetadoReady && reserva.userCreadorReady
    val contrincanteReady = if (soyElCreador) reserva.userRetadoReady else reserva.userCreadorReady
    val yoEstoyReady = if (soyElCreador) reserva.userCreadorReady else reserva.userRetadoReady
    val contrincanteNombre = if (soyElCreador) reserva.retado else reserva.creador
    val isPagado = reserva.montoTotal - reserva.montoPagado == 0.0

    val estadoBotonReady = when {
        bothUsersReady -> "EMPEZAR RETO"
        yoEstoyReady && !contrincanteReady -> "ESPERANDO RIVAL..."
        !yoEstoyReady && contrincanteReady -> "¡RIVAL LISTO!"
        else -> "PREPARARME"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = colorBlackSurface)
    ) {
        Column {
            // Sección Superior: Imagen con Gradiente y Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = reserva.sedeImagen,
                    contentDescription = "Sede ${reserva.sede}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradiente oscuro para que el texto blanco siempre sea legible
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                startY = 300f
                            )
                        )
                )

                // Contenedor de Estados y Saldo (Top End)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Badge de Estado
                    BadgeEstado(reserva = reserva)

                    // Badge de Saldo Pendiente mejorado
                    val saldoPendiente = reserva.montoTotal - reserva.montoPagado
                    if (saldoPendiente > 0 && estado.mostrarSaldo()) {
                        BadgeMontoPendiente(saldoPendiente)
                    }
                }

                // Nombre de la Sede sobre la imagen (Bottom Start)
                Text(
                    text = reserva.sede,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            // Sección Inferior: Detalles de tiempo y mesa
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fila de Inicio y Fin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoReservaCol("INICIO", reserva.fechaInicio, Icons.Default.CalendarToday)
                    InfoReservaCol("FIN", reserva.fechaFin, Icons.Default.AccessTime)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Badge("Mesa", reserva.mesa, Icons.Default.TableBar)
                    Badge("Contra", contrincanteNombre, Icons.Default.Person)
                }

                val mostrarAcciones = (estado.puedeGestionarReto() && soyElRetado) ||
                        (estado.puedeCancelarse() && soyElCreador) ||
                        estado.puedeVerJuego() || estado.puedeVerResultado() || estado.puedeEscogerItem() || (!isPagado && estado.puedePagarse())

                if (mostrarAcciones) {
                    HorizontalDivider(
                        Modifier,
                        DividerDefaults.Thickness,
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // --- 1. GESTIONAR RETO (Solo el retado) ---
                        if (estado.puedeGestionarReto() && soyElRetado) {
                            CustomButtonFilled(
                                modifier = Modifier.weight(1f),
                                enabled = true,
                                onClick = { onAceptarRechazarReto(reserva) },
                                text = "VER RETO"
                            )
                        }

                        // --- 2. ELIMINAR (Solo creador y si el estado lo permite) ---
                        if (estado.puedeCancelarse() && soyElCreador) {
                            CustomOutlineButtonTextIcon(
                                modifier = Modifier.weight(1f),
                                colorOutline = colorRedError,
                                colorText = colorRedError,
                                icon = Icons.Default.DeleteForever,
                                enabled = true,
                                onClick = { onEliminar(reserva) },
                                text = "ELIMINAR"
                            )
                        }

                        // --2.5. PENDIENTE PAGO
                        if (!isPagado && estado.puedePagarse()) {
                            CustomButtonFilled(
                                modifier = Modifier.weight(1f),
                                enabled = true,
                                onClick = { onShowQrPago(reserva) },
                                text = "Ver QR de Pago"
                            )
                        }
                        // --- 3. INVENTARIO (Se muestra si el estado permite jugar y aún no empieza el juego) ---
                        if (estado.puedeEscogerItem() && !bothUsersReady) {
                            CustomOutlineButtonTextIcon(
                                modifier = Modifier.weight(1f),
                                colorOutline = if (contrincanteReady) colorCeleste else colorBlanco.copy(alpha = 0.4f),
                                icon = Icons.Default.Inventory2,
                                enabled = true,
                                onClick = { onShowInventario(reserva) },
                                text = estadoBotonReady
                            )
                        }

                        // --- 4. INICIAR / VER JUEGO ---
                        if (estado.puedeVerJuego() && !soyUsuarioPendienteRpta && bothUsersReady) {
                            // Botón Iniciar Juego
                            CustomButtonFilled(
                                modifier = Modifier.weight(1f),
                                colorBackGround = if (estado == ReservaEstado.EN_JUEGO) colorVerdeClaro else colorPrimary,
                                enabled = true,
                                icon = Icons.Default.PlayArrow,
                                onClick = { onStartGameClick(reserva) },
                                text = if (estado == ReservaEstado.EN_JUEGO) "VER JUEGO" else "INICIAR"
                            )
                        }

                        // --- 5. REVISAR RESULTADO (Acuerdo Mutuo) ---
                        if (estado.puedeDarResultado() && soyUsuarioPendienteRpta && reserva.esperandoConfirmacion) {
                            CustomButtonFilled(
                                modifier = Modifier.weight(1f),
                                colorBackGround = colorCelesteNeon,
                                colorText = colorBlackSurface,
                                enabled = true,
                                icon = Icons.Default.Analytics,
                                onClick = { onVerificarResultado(reserva) },
                                text = "REVISAR"
                            )
                        }

                        // --- 6. VER RESULTADOS FINALES ---
                        if (estado.puedeVerResultado() && !reserva.esperandoConfirmacion) {
                            CustomButtonFilled(
                                modifier = Modifier.weight(1f),
                                enabled = true,
                                icon = Icons.Default.Leaderboard,
                                onClick = { onVerResultados(reserva) },
                                text = "VER RESULTADOS"
                            )
                        }
                    }
                }
            }
        }
    }
}