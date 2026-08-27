package pe.breaker.dkaviplay.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun provideHttpClient(): HttpClient = HttpClient(Darwin)
actual fun getEngine(): HttpClientEngineFactory<*> = Darwin