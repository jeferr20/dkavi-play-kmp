package pe.breaker.dkaviplay.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeMemoryPolicy
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule = module {

    single(named("prefs")) {
        KSafe(
            fileName = "prefs",
            memoryPolicy = KSafeMemoryPolicy.PLAIN_TEXT
        )
    }

    single(named("vault")) {
        KSafe(
            fileName = "vault"
        )
    }

    single<Settings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
}