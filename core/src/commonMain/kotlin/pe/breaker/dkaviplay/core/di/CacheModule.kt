package pe.breaker.dkaviplay.core.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.core.cache.Database
import pe.breaker.dkaviplay.core.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.core.data.util.SecureStorage

val cacheModule = module {
    single {
        SecureStorage(vault = get(named("vault")))
    }

    single {
        val driverFactory: DatabaseDriverFactory = get()
        AppDatabase(driverFactory.createDriver())
    }

    single { Database(get()) }
}