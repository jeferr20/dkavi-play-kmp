package pe.breaker.dkaviplay.presentation.screen.perfil

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.domain.model.inventory.TipoPremio
import pe.breaker.dkaviplay.presentation.screen.arbitro.ArbitroScreen
import pe.breaker.dkaviplay.presentation.screen.inventario.InventarioScreen
import pe.breaker.dkaviplay.presentation.screen.login.LoginScreen
import pe.breaker.dkaviplay.presentation.screen.rangos.RangoScreenModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosScreen
import pe.breaker.dkaviplay.presentation.screen.rangos.RangosScreen
import pe.breaker.dkaviplay.presentation.screen.reglas.ReglasInformacionScreen

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 4u,
            title = "Perfil",
            icon = rememberVectorPainter(Icons.Default.Person)
        )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val rootNavigator = navigator.parent ?: navigator
        val profileModel = koinScreenModel<ProfileScreenModel>()
        val rangoScreenModel = koinScreenModel<RangoScreenModel>()
        val profileState by profileModel.state.collectAsState()
        val rangoState by rangoScreenModel.state.collectAsState()
        val scope = rememberCoroutineScope()

        ProfileScreen(
            profileState = profileState,
            rangoState = rangoState,
            onLogout = {
                scope.launch {
                    profileModel.logOut()
                    rootNavigator.replaceAll(LoginScreen())
                }
            },
            onUploadPhoto = { profileModel.uploadImage(it) },
            onOpenMisDatos = { rootNavigator.push(
                RegisterDatosScreen(
                    true,
                    null
                )
            ) },
            onOpenRangos = { rootNavigator.push(RangosScreen()) },
            onOpenArbitro = { rootNavigator.push(ArbitroScreen())},
            onOpenReglas = { rootNavigator.push(ReglasInformacionScreen()) },
            onOpenInventario = { rootNavigator.push(InventarioScreen(TipoPremio.ITEM_JUGABLE)) },
            onOpenPremios = { rootNavigator.push(InventarioScreen(TipoPremio.PREMIO_FISICO)) },
            onOpenCupones = { rootNavigator.push(InventarioScreen(TipoPremio.MONETARIO)) },
            onOpenTiposRetos = { rootNavigator.push(InventarioScreen(TipoPremio.RETO)) },
        )
    }
}