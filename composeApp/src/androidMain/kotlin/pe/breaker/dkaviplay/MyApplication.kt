package pe.breaker.dkaviplay

import android.app.Application
import dkaviplay.composeapp.generated.resources.Res
import kotlinx.coroutines.runBlocking // 🟢 Cambiado para forzar la lectura síncrona de los JSONs
import org.koin.android.ext.android.getKoin // 🟢 Extensión nativa de Koin para Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import pe.breaker.dkaviplay.core.di.androidCoreModule
import pe.breaker.dkaviplay.core.di.androidFirebaseInitializerModule
import pe.breaker.dkaviplay.core.di.cacheModule
import pe.breaker.dkaviplay.core.di.firebaseModule
import pe.breaker.dkaviplay.core.di.networkModule
import pe.breaker.dkaviplay.core.di.repositoryModule
import pe.breaker.dkaviplay.core.domain.repository.UbigeoRepository
import pe.breaker.dkaviplay.di.androidModule
import pe.breaker.dkaviplay.di.commonModule
import pe.breaker.dkaviplay.di.platformModule
import pe.breaker.dkaviplay.util.ContextProvider

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContextProvider.initialize(this)

        startKoin {
            androidContext(this@MyApplication)
            androidLogger(Level.DEBUG)
            modules(
                cacheModule,
                commonModule,
                androidModule,
                firebaseModule,
                platformModule,
                networkModule,
                androidCoreModule,
                repositoryModule,
                androidFirebaseInitializerModule
            )
        }

        val ubigeoRepository = getKoin().get<UbigeoRepository> {
            parametersOf(
                { path: String ->
                    runBlocking {
                        try {
                            Res.readBytes(path).decodeToString()
                        } catch (e: Exception) {
                            println("Error leyendo bytes de Ubigeo desde composeApp: ${e.message}")
                            ""
                        }
                    }
                }
            )
        }
    }
}