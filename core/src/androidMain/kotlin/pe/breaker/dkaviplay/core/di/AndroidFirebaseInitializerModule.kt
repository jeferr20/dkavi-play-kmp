package pe.breaker.dkaviplay.core.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidFirebaseInitializerModule = module {
    single {
        Firebase.initialize(androidContext())
    }
}