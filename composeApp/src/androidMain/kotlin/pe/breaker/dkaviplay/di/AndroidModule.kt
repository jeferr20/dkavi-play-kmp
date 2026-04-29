package pe.breaker.dkaviplay.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.AndroidDatabaseDriverFactory
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.presentation.animations.audio.AudioFactory
import pe.breaker.dkaviplay.presentation.util.AndroidAudioFactory
import pe.breaker.dkaviplay.presentation.util.AndroidImageResizer
import pe.breaker.dkaviplay.presentation.util.AndroidToastHandler
import pe.breaker.dkaviplay.presentation.util.ImageResizer
import pe.breaker.dkaviplay.presentation.util.ToastHandler

val androidModule = module{
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
    single<ToastHandler> { AndroidToastHandler(get()) }
    single<ImageResizer> { AndroidImageResizer(get()) }
    single<AudioFactory> { AndroidAudioFactory(get()) }
    single<PlatformContext> { AndroidPlatformContext(androidContext()) }
}