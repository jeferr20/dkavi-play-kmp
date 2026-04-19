package pe.breaker.dkaviplay.di

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

actual fun provideHttpClient(): HttpClient =
    HttpClient(Darwin) {
        install(ContentNegotiation) {
            json()
        }
    }