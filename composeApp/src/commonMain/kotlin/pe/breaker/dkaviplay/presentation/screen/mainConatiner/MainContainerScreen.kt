package pe.breaker.dkaviplay.presentation.screen.mainConatiner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.koin.compose.koinInject
import pe.breaker.dkaviplay.presentation.component.FloatingBottomBar
import pe.breaker.dkaviplay.presentation.navigation.GlobalNavigationBus
import pe.breaker.dkaviplay.presentation.navigation.NavigationEvent
import pe.breaker.dkaviplay.presentation.screen.map.MapTab
import pe.breaker.dkaviplay.util.PermissionManager

class MainContainerScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
        val model = koinScreenModel<MainContainerModel>()
        val permissionManager = koinInject<PermissionManager>()

        LaunchedEffect(Unit) {
            permissionManager.requestNotificationPermission()
            model.syncNotificationToken()
        }

        TabNavigator(MapTab) { tabNavigator ->
            val navigationEvent by GlobalNavigationBus.currentTabTarget.collectAsState()

            LaunchedEffect(navigationEvent) {
                navigationEvent?.let { event ->
                    when (event) {
                        is NavigationEvent.GoToAceptarReto -> {
//                            rootNavigator.push(AceptarRetoScreen(event.reservaId))
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToVerificarResultados -> {
//                            rootNavigator.push(AcuerdoMutuoScreen(event.reservaId))
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToReservations -> {
//                            tabNavigator.current = ReservasTab
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToProfile -> {
//                            tabNavigator.current = ProfileTab
                            GlobalNavigationBus.clear()
                        }

                        is NavigationEvent.GoToResultadoPartida -> {
//                            rootNavigator.push(ResultadoPartidaScreen(event.reservaId))
                            GlobalNavigationBus.clear()
                        }
                    }
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                containerColor = Color.Black,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    FloatingBottomBar(
//                        tabs = listOf(MapTab, ReservasTab, ProfileTab)
                        tabs = listOf(MapTab)
                    )
                }
            ) { _ ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    CurrentTab()
                }
            }
        }
    }
}