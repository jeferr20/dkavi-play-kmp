package pe.breaker.dkaviplay.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory
import pe.breaker.dkaviplay.presentation.util.AndroidAudioFactory
import pe.breaker.dkaviplay.presentation.util.AndroidImageResizer
import pe.breaker.dkaviplay.presentation.util.AndroidToastHandler
import pe.breaker.dkaviplay.presentation.util.ImageResizer
import pe.breaker.dkaviplay.presentation.util.ShareHandler
import pe.breaker.dkaviplay.presentation.util.ToastHandler
import pe.breaker.dkaviplay.util.AndroidShareHandler

val androidModule = module{
    single<ToastHandler> { AndroidToastHandler(get()) }
    single<ImageResizer> { AndroidImageResizer(get()) }
    single<AudioFactory> { AndroidAudioFactory(get()) }
    single<PlatformContext> { AndroidPlatformContext(androidContext()) }
    single<ShareHandler> { AndroidShareHandler(androidContext()) }
}