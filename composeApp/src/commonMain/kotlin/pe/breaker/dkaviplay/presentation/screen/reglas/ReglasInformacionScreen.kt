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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import pe.breaker.dkaviplay.domain.model.TipoJuego
import pe.breaker.dkaviplay.presentation.components.CustomAppbar
import pe.breaker.dkaviplay.presentation.screen.reglas.components.CardClubDKAVI
import pe.breaker.dkaviplay.presentation.screen.reglas.components.CardItem
import pe.breaker.dkaviplay.presentation.screen.reglas.components.SectionHeader

class ReglasInformacionScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()

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

                        TipoJuego.entries.forEach { tipo ->
                            CardItem(
                                title = tipo.nombre,
                                descripcion = tipo.descripcion,
                                icon = when (tipo) {
                                    TipoJuego.BILLAR -> Icons.Default.SportsCricket
                                    TipoJuego.POOL -> Icons.Default.Layers
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}