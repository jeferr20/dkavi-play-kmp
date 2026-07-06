package pe.breaker.dkaviplay

import android.app.Application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import pe.breaker.dkaviplay.di.androidModule
import pe.breaker.dkaviplay.di.dataModule
import pe.breaker.dkaviplay.di.domainModule
import pe.breaker.dkaviplay.di.firebaseModule
import pe.breaker.dkaviplay.di.networkModule
import pe.breaker.dkaviplay.di.platformModule
import pe.breaker.dkaviplay.di.presentationModule
import pe.breaker.dkaviplay.util.ContextProvider

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextProvider.initialize(this)
        Firebase.initialize(this)
        startKoin {
            androidContext(this@MyApplication)
            androidLogger(Level.DEBUG)
            modules(
                firebaseModule,
                networkModule,
                dataModule,
                domainModule,
                presentationModule,
                androidModule,
                platformModule
            )
        }
    }
}