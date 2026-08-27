package pe.breaker.dkaviplay.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory

expect fun provideHttpClient(): HttpClient
expect fun getEngine(): HttpClientEngineFactory<*>