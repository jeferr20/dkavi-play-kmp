package pe.breaker.dkaviplay.presentation.util.lectorQR

import io.github.ismoy.imagepickerkmp.domain.models.GalleryPhotoResult

// commonMain
expect suspend fun decodeQrFromPhoto(photo: GalleryPhotoResult,context: Any? = null): String?