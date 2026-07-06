import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import pe.breaker.dkaviplay.di.dataModule
import pe.breaker.dkaviplay.di.domainModule
import pe.breaker.dkaviplay.di.firebaseModule
import pe.breaker.dkaviplay.di.iosModule
import pe.breaker.dkaviplay.di.networkModule
import pe.breaker.dkaviplay.di.platformModule
import pe.breaker.dkaviplay.di.presentationModule

fun initKoin() {
    startKoin {
        printLogger(Level.DEBUG)
        modules(
            firebaseModule,
            networkModule,
            dataModule,
            domainModule,
            presentationModule,
            iosModule,
            platformModule
        )
    }
}