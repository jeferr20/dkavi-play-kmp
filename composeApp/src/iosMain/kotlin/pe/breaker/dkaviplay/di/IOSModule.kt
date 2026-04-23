package pe.breaker.dkaviplay.di

import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.cache.IOSDatabaseDriverFactory
import pe.breaker.dkaviplay.presentation.util.ImageResizer
import pe.breaker.dkaviplay.presentation.util.IosImageResizer
import pe.breaker.dkaviplay.presentation.util.IosToastHandler
import pe.breaker.dkaviplay.presentation.util.ToastHandler

val iosModule = module {
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
    single<ToastHandler> { IosToastHandler() }
    single<ImageResizer> { IosImageResizer() }
    single<PlatformContext> { IosPlatformContext() }
}