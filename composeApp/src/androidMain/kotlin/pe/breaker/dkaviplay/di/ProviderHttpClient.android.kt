package pe.breaker.dkaviplay.di

import io.ktor.client.HttpClient

import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

actual fun provideHttpClient(): HttpClient =
    HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }