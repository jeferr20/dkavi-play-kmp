package pe.breaker.dkaviplay.di

import android.content.Context

class AndroidPlatformContext(private val context: Context) : PlatformContext {
    override val androidContext: Context = context
}