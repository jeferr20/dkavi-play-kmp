package pe.breaker.dkaviplay.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.cache.IOSDatabaseDriverFactory
import platform.Foundation.NSUserDefaults

val iosModule = module {
    single<Settings> {
        val userDefaults = NSUserDefaults.standardUserDefaults
        NSUserDefaultsSettings(userDefaults)
    }
    single<Settings.Factory> {
        NSUserDefaultsSettings.Factory()
    }
    single<DatabaseDriverFactory> { IOSDatabaseDriverFactory() }
    single<Any?>(named("appContext")) { null }
    single<PlatformContext> { IosPlatformContext() }
}