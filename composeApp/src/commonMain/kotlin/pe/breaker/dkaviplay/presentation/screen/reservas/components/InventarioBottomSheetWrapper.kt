package pe.breaker.dkaviplay.presentation.screen.reservas.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pe.breaker.dkaviplay.domain.model.Reserva
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.components.dialog.StatusDialog
import pe.breaker.dkaviplay.presentation.screen.inventario.InventarioModel
import pe.breaker.dkaviplay.presentation.screen.inventario.InventarioState
import pe.breaker.dkaviplay.presentation.theme.colorBlackSurface
import pe.breaker.dkaviplay.presentation.util.StatusUiType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioBottomSheetWrapper(
    reserva: Reserva,
    onDismiss: () -> Unit,
    inventarioModel: InventarioModel,
    state: InventarioState
) {
    val sheetState = rememberModalBottomSheetState()

    state.successMessage?.let { message ->
        StatusDialog(
            status = StatusUiType.SUCCESS,
            message = message,
            onDismiss = { inventarioModel.clearSuccessMessage() },
            onConfirm = {
                inventarioModel.clearSuccessMessage()
                onDismiss()
            },
            confirmButtonText = "Aceptar",
            hideCancelar = true
        )
    }

    state.errorMessage?.let { message ->
        StatusDialog(
            status = StatusUiType.ERROR,
            message = message,
            onDismiss = { inventarioModel.resetError() },
            onConfirm = { inventarioModel.resetError() },
            confirmButtonText = "Aceptar",
            hideCancelar = true
        )
    }

    if (state.isLoading && state.items.isNotEmpty()) {
        LoadingDialog(message = "Equipando item...")
    }

    // --- BOTTOM SHEET ---
    ModalBottomSheet(
        onDismissRequest = {
            if (!state.isLoading) onDismiss()
        },
        sheetState = sheetState,
        containerColor = colorBlackSurface,
        scrimColor = Color.Black.copy(alpha = 0.8f)
    ) {
        // Asegúrate de que esta función sea accesible (pública)
        InventarioSheetContent(
            items = state.items,
            isLoading = state.isLoading,
            onItemConfirm = { item ->
                inventarioModel.actualizarRecompensas(reserva, item.id)
            }
        )
    }
}