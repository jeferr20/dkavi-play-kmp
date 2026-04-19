import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import pe.breaker.dkaviplay.data.provider.FirebaseAuthProvider
//import pe.breaker.dkaviplay.data.provider.FirebaseAuthProviderIOS
import pe.breaker.dkaviplay.di.commonModule
import pe.breaker.dkaviplay.di.iosModule
import pe.breaker.dkaviplay.di.networkModule
import kotlin.experimental.ExperimentalObjCName

//@OptIn(ExperimentalObjCName::class)
//@ObjCName("KoinIOS") // nombre que verá Swift
//object KoinIOS {
//    fun initialize() = startKoin {
//        printLogger(Level.DEBUG)
//        modules(
//            commonModule,
//            networkModule,
//            iosModule,
//            module {
//                single<FirebaseAuthProvider> { FirebaseAuthProviderIOS() }
////                single<FirestoreProvider> { FirestoreProviderIOS() }
//            }
//        )
//    }
//}

fun initKoinIOS() = startKoin {
    printLogger(Level.DEBUG)
    modules(
        commonModule,
        networkModule,
        iosModule,
        module {
//            single<FirebaseAuthProvider> { FirebaseAuthProviderIOS() }
//            single<FirestoreProvider> { FirestoreProviderIOS() }
        }
    )
}