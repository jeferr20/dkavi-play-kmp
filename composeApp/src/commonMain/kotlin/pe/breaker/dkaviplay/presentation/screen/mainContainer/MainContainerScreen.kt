package pe.breaker.dkaviplay.presentation.screen.mainContainer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import dev.icerock.moko.permissions.notifications.REMOTE_NOTIFICATION
import pe.breaker.dkaviplay.presentation.navigation.GlobalNavigationBus
import pe.breaker.dkaviplay.presentation.navigation.NavigationEvent
import pe.breaker.dkaviplay.presentation.screen.aceptarReto.AceptarRetoScreen
import pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo.AcuerdoMutuoScreen
import pe.breaker.dkaviplay.presentation.screen.mainContainer.components.FloatingBottomBar
import pe.breaker.dkaviplay.presentation.screen.map.MapTab
import pe.breaker.dkaviplay.presentation.screen.perfil.ProfileTab
import pe.breaker.dkaviplay.presentation.screen.reservas.ReservasTab
import pe.breaker.dkaviplay.presentation.screen.resultadosPartida.ResultadoPartidaScreen

class MainContainerScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
//        val model = koinScreenModel<MainContainerModel>()

        val permissionsController =
            rememberPermissionsControllerFactory().createPermissionsController()
        BindEffect(permissionsController)

        LaunchedEffect(Unit) {
            try {
                permissionsController.providePermission(Permission.REMOTE_NOTIFICATION)
//                model.syncNotificationToken()
            } catch (e: DeniedAlwaysException) {
                println("Permiso denegado permanentemente: ${e.message}")
            } catch (e: DeniedException) {
                println("Permiso denegado: ${e.message}")
            }
//            model.syncNotificationToken()
        }

        TabNavigator(MapTab) { tabNavigator ->
            val navigationEvent by GlobalNavigationBus.currentTabTarget.collectAsState()

            LaunchedEffect(navigationEvent) {
                navigationEvent?.let { event ->
                    when (event) {
                        is NavigationEvent.GoToAceptarReto -> {
                            rootNavigator.push(AceptarRetoScreen(event.reservaId))
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToVerificarResultados -> {
                            rootNavigator.push(AcuerdoMutuoScreen(event.reservaId))
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToReservations -> {
                            tabNavigator.current = ReservasTab
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToProfile -> {
                            tabNavigator.current = ProfileTab
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToResultadoPartida -> {
                            rootNavigator.push(ResultadoPartidaScreen(event.reservaId))
                            GlobalNavigationBus.clear()
                        }
                    }
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Black,
                bottomBar = {
                    FloatingBottomBar(
                        tabs = listOf(MapTab, ReservasTab, ProfileTab)
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CurrentTab()
                }
            }
        }
    }
}