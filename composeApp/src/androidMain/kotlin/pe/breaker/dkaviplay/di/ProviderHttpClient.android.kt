package pe.breaker.dkaviplay.di

import io.ktor.client.HttpClient

import io.ktor.client.engine.android.Android

actual fun provideHttpClient(): HttpClient = HttpClient(Android)