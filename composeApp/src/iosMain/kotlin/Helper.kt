import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import pe.breaker.dkaviplay.di.commonModule
import pe.breaker.dkaviplay.di.iosModule
import pe.breaker.dkaviplay.di.networkModule
import pe.breaker.dkaviplay.di.platformModule

fun initKoin(){
    startKoin{
        printLogger(Level.DEBUG)
        modules(commonModule,iosModule, platformModule, networkModule)
    }
}