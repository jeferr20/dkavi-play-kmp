package pe.breaker.dkaviplay.presentation.screen.perfil

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.core.data.util.UserSessionManager
import pe.breaker.dkaviplay.core.domain.repository.AuthRepository
import pe.breaker.dkaviplay.core.domain.usecase.UploadProfileImageUseCase
import pe.breaker.dkaviplay.presentation.util.ImageResizer

class ProfileScreenModel(
    private val sessionManager: UserSessionManager,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val imageResizer: ImageResizer,
    private val authRepository: AuthRepository
) : StateScreenModel<ProfileScreenState>(ProfileScreenState()){
    private val _profileImage = MutableStateFlow<String?>(null)

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val nombre = sessionManager.getCurrentUsuario()?.usuario ?: "User"
        val urlImagen = sessionManager.getCurrentUsuario()?.urlImagen
        mutableState.update {
            it.copy(
                nombre = nombre,
                urlImagenPerfil = urlImagen
            )
        }
    }

    fun uploadImage(photo: GalleryPhotoResult) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }

            val bytes = imageResizer.compressAndResize(photo.uri)

            if (bytes != null) {
                uploadProfileImageUseCase(bytes)
                    .onSuccess { url ->
                        _profileImage.value = url
                        mutableState.update { it.copy(isLoading = false, urlImagenPerfil = url,successMessage = "Imagen actualizada") }
                    }
                    .onFailure { error ->
                        mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
            } else {
                mutableState.update { it.copy(isLoading = false, errorMessage = "Error al procesar imagen") }
            }
        }
    }

    suspend fun logOut(){
        sessionManager.clearSession()
        authRepository.logout()
    }
}