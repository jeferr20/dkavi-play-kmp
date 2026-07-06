package pe.breaker.dkaviplay.core.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import dev.gitlive.firebase.messaging.messaging
import dev.gitlive.firebase.remoteconfig.remoteConfig
import dev.gitlive.firebase.storage.storage
import org.koin.dsl.module

val firebaseModule = module {
    factory { Firebase.auth }
    factory { Firebase.firestore }
    factory { Firebase.remoteConfig }
    factory { Firebase.storage }
    factory { Firebase.messaging }
}