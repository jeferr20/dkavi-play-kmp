package pe.breaker.dkaviplay.presentation.screen.reglas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.screen.reglas.components.CardClubDKAVI
import pe.breaker.dkaviplay.presentation.screen.reglas.components.CardItem
import pe.breaker.dkaviplay.presentation.screen.reglas.components.SectionHeader
import pe.breaker.dkaviplay.presentation.screen.reglas.model.ReglaItem

class ReglasInformacionScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()

        val reglasPoolStreet = listOf(
            ReglaItem("Pool", "Se rige estrictamente por el REGLAMENTO PANAMERICANO DE POOL.",Icons.Default.Gavel),
            ReglaItem("Billas", "Se rige por el REGLAMENTO DE BILLAS CLUB DKAVI.",Icons.Default.Gavel),
//            ReglaItem("Revancha Automática", "El perdedor tiene derecho a revancha inmediata (solo por puntos y honor).",Icons.Default.Gavel),
//            ReglaItem("Desafío Final", "No se permiten atributos de ningún tipo durante el desafío final.",Icons.Default.Gavel),
//            ReglaItem("Límite de Atributos", "Se permite únicamente el uso de un atributo por cada desafío.",Icons.Default.Gavel)
        )

        Scaffold(
            containerColor = Color.Black,
            modifier = Modifier.imePadding()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    CustomAppbar(text = "Reglas e Información", onClick = { navigator.pop() })

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CardClubDKAVI()

                        SectionHeader(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Definición",
                            icon = Icons.Default.Info
                        )

                        CardItem(
                            descripcion = "CLUB DKAVI es un programa exclusivo para atletas de billar, diseñado con el firme objetivo de fomentar el desarrollo del billar oficial, el ranking competitivo y el reconocimiento institucional por parte de la FPB (Federación Peruana de Billar)."
                        )

                        SectionHeader(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Reglas",
                            icon = Icons.Default.Gavel
                        )

                        reglasPoolStreet.forEach { regla ->
                            CardItem(
                                title = regla.title,
                                descripcion = regla.descripcion,
                                icon = regla.icon,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}