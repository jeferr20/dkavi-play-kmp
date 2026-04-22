package pe.breaker.dkaviplay.presentation.screen.perfil

data class ProfileScreenState (
    val nombre: String? = null,
    val urlImagenPerfil: String? = null,

    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)