package pe.breaker.dkaviplay.core.di

import org.koin.dsl.module
import pe.breaker.dkaviplay.core.cache.AndroidDatabaseDriverFactory
import pe.breaker.dkaviplay.core.cache.DatabaseDriverFactory

val androidCoreModule = module{
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(get()) }
}