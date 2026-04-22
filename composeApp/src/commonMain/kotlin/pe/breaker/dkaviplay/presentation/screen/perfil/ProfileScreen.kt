package pe.breaker.dkaviplay.presentation.screen.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Backpack
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import io.github.ismoy.imagepickerkmp.presentation.ui.components.GalleryPickerLauncher
import pe.breaker.dkaviplay.presentation.components.dialog.LoadingDialog
import pe.breaker.dkaviplay.presentation.screen.perfil.components.OptionGroupCard
import pe.breaker.dkaviplay.presentation.screen.perfil.components.ProfileHeader
import pe.breaker.dkaviplay.presentation.screen.perfil.components.ProfileOptionItem
import pe.breaker.dkaviplay.presentation.screen.perfil.components.SectionTitle
import pe.breaker.dkaviplay.presentation.screen.perfil.components.UserQrDialog
import pe.breaker.dkaviplay.presentation.screen.rangos.RangoScreenState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileState: ProfileScreenState,
    rangoState: RangoScreenState,
    onLogout: () -> Unit,
    onUploadPhoto: (GalleryPhotoResult) -> Unit,
    onOpenMisDatos: () -> Unit,
    onOpenArbitro: () -> Unit,
    onOpenRangos: () -> Unit,
    onOpenReglas: () -> Unit,
    onOpenPremios: () -> Unit,
    onOpenInventario: () -> Unit,
    onOpenCupones: () -> Unit,
    onOpenTiposRetos: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showQrDialog by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item{
                ProfileHeader(
                    modifier = Modifier.fillMaxWidth(),
                    nombre = profileState.nombre ?: "",
                    rango = rangoState.rangoActual?.categoria ?: "",
                    urlImage = profileState.urlImagenPerfil,
                    onImagenClick = { showSheet = true }
                )
            }

            item { SectionTitle("MI CARRERA") }
            item {
                OptionGroupCard {
                    ProfileOptionItem(Icons.Default.QrCode, "Mi QR", onClick = { showQrDialog = true })
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.05f))
                    ProfileOptionItem(Icons.Default.EmojiEvents, "Progreso de Rangos",onClick = onOpenRangos)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.05f))
                    ProfileOptionItem(Icons.Default.SportsKabaddi, "Mis Tipos de Retos", onClick = onOpenTiposRetos)
                }
            }

            item { SectionTitle("MOCHILA Y PREMIOS") }
            item {
                OptionGroupCard {
                    ProfileOptionItem(Icons.Default.Backpack, "Mis Poderes", onClick = onOpenInventario)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.05f))
                    ProfileOptionItem(Icons.Default.Inventory, "Mis Premios Físicos", onClick = onOpenPremios)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.05f))
                    ProfileOptionItem(Icons.Default.ConfirmationNumber, "Mis Cupones", onClick = onOpenCupones)
                }
            }

            item { SectionTitle("AJUSTES") }
            item {
                OptionGroupCard {
                    ProfileOptionItem(Icons.Default.Person, "Mis Datos Personales", onClick = onOpenMisDatos)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.05f))
                    ProfileOptionItem(Icons.Default.Sports, "Árbitro", onClick = onOpenArbitro)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color.White.copy(alpha = 0.05f))
                    ProfileOptionItem(Icons.Default.Info, "Reglas del Club", onClick = onOpenReglas)
                }
            }

            item {
                ProfileOptionItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    text = "Cerrar sesión",
                    textColor = Color(0xFFEF4444),
                    onClick = onLogout
                )
            }

//            item {
//                Spacer(modifier = Modifier.height(70.dp))
//            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1A1C1E),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, top = 8.dp)
                ) {
                    Text(
                        text = "Foto de perfil",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ListItem(
                        headlineContent = { Text("Actualizar Foto", color = Color.White) },
                        leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
                        modifier = Modifier.clickable { showGallery = true },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }

        if (showGallery) {
            GalleryPickerLauncher(
                includeExif = true,
                onPhotosSelected = { photos ->
                    photos.firstOrNull()?.let { photo ->
                        onUploadPhoto(photo)
                    }
                    showGallery = false
                    showSheet = false
                },
                onError = { showGallery = false },
                onDismiss = { showGallery = false },
                allowMultiple = false
            )
        }

        if (showQrDialog) {
            UserQrDialog(
                text = profileState.nombre ?: "",
                onDismiss = { showQrDialog = false }
            )
        }
    }

    if(profileState.isLoading){
        LoadingDialog()
    }
}