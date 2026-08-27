package pe.breaker.dkaviplay.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun provideHttpClient(): HttpClient = HttpClient(OkHttp)
actual fun getEngine(): HttpClientEngineFactory<*> = OkHttp