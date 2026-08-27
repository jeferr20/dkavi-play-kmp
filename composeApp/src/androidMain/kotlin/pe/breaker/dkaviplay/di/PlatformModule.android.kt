package pe.breaker.dkaviplay.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.KSafeMemoryPolicy
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import android.content.Context

actual val platformModule = module {

    single(named("prefs")) {
        KSafe(
            context = androidApplication(),
            fileName = "prefs",
            memoryPolicy = KSafeMemoryPolicy.PLAIN_TEXT
        )
    }

    single(named("vault")) {
        KSafe(
            context = androidApplication(),
            fileName = "vault"
        )
    }

    single<Settings> {
        val sharedPrefs = androidApplication().getSharedPreferences("dkavi_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(sharedPrefs)
    }
}