package pe.breaker.dkaviplay.presentation.screen.map

import pe.breaker.dkaviplay.domain.model.Sede

data class MapTabState (
    val sedes: List<Sede>? = null,
    val ocupacionActual: String = "0/0",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)