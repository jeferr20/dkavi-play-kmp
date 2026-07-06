package pe.breaker.dkaviplay.presentation.screen.perfil

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.breaker.dkaviplay.di.UserSessionManager
import pe.breaker.dkaviplay.domain.usecase.DeleteAccountUseCase
import pe.breaker.dkaviplay.domain.usecase.LogOutUseCase
import pe.breaker.dkaviplay.domain.usecase.UploadProfileImageUseCase
import pe.breaker.dkaviplay.presentation.util.ImageResizer

class ProfileScreenModel(
    private val sessionManager: UserSessionManager,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val imageResizer: ImageResizer,
    private val logOutUseCase: LogOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : StateScreenModel<ProfileScreenState>(ProfileScreenState()){

    init {
        observeUserData()
    }

    private fun observeUserData() {
        screenModelScope.launch {
            sessionManager.getCurrentUsuarioFlow()
                .filterNotNull()
                .collect { usuarioTable ->
                    mutableState.update {
                        it.copy(
                            nombre = usuarioTable.usuario,
                            urlImagenPerfil = usuarioTable.urlImagen,
                            monedas = usuarioTable.monedas.toInt()
                        )
                    }
                }
        }
    }

    fun uploadImage(photo: GalleryPhotoResult) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }

            val bytes = imageResizer.compressAndResize(photo.uri)
            if (bytes != null) {
                uploadProfileImageUseCase(bytes)
                    .onSuccess { url ->
                        mutableState.update {
                            it.copy(isLoading = false, urlImagenPerfil = url, successMessage = "Imagen actualizada")
                        }
                    }
                    .onFailure { error ->
                        mutableState.update { it.copy(isLoading = false, errorMessage = error.message) }
                    }
            } else {
                mutableState.update { it.copy(isLoading = false, errorMessage = "Error al procesar imagen") }
            }
        }
    }

    fun logOut(){
        screenModelScope.launch {
            logOutUseCase()
        }
    }

    fun deleteAccount(onSuccessAction: () -> Unit) {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            deleteAccountUseCase()
                .onSuccess {
                    mutableState.update { it.copy(isLoading = false, successMessage = "Cuenta eliminada correctamente") }
                    onSuccessAction()
                }
                .onFailure { error ->
                    mutableState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Error al eliminar la cuenta") }
                }
        }
    }
}