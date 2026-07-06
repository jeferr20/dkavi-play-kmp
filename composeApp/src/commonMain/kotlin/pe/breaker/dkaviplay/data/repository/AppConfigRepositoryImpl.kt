package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import pe.breaker.dkaviplay.domain.model.AppConfig
import pe.breaker.dkaviplay.domain.repository.AppConfigRepository

class AppConfigRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig,
    private val firestore: FirebaseFirestore
) : AppConfigRepository {
    object RemoteConfigKeys {
        const val ACTIVE = "activeServiceMovil"
        const val VERSION = "versionMovil"
        const val URL_ANDROID = "urlMovilAndroid"
        const val URL_IOS = "urlMovilIOS"
    }

    override fun observeAppConfig(): Flow<AppConfig> = flow {

        println("🔥 START FLOW")

        remoteConfig.setDefaults(
            RemoteConfigKeys.ACTIVE to true,
            RemoteConfigKeys.VERSION to "1.0.0",
            RemoteConfigKeys.URL_ANDROID to "",
            RemoteConfigKeys.URL_IOS to ""
        )

        val initial = try {
            println("🔥 FETCH RC")
            remoteConfig.fetchAndActivate()

            val config = AppConfig(
                activeServiceMovil = remoteConfig.getValue(RemoteConfigKeys.ACTIVE).asBoolean(),
                versionMovil = remoteConfig.getValue(RemoteConfigKeys.VERSION).asString(),
                urlMovilAndroid = remoteConfig.getValue(RemoteConfigKeys.URL_ANDROID).asString(),
                urlMovilIOS = remoteConfig.getValue(RemoteConfigKeys.URL_IOS).asString()
            )

            println("🔥 RC OK: $config")
            config

        } catch (e: Exception) {
            println("🔥 RC ERROR: ${e.message}")
            AppConfig()
        }

        emit(initial)

        val firestoreFlow = firestore
            .collection("AppConfig")
            .document("global")
            .snapshots
            .map { snapshot ->
                println("🔥 SNAPSHOT RECIBIDO")

                if (!snapshot.exists) {
                    println("❌ DOCUMENT NO EXISTE")
                    return@map initial
                }

                try {
                    val config = snapshot.data<AppConfig>()
                    println("🔥 FIRESTORE OK: $config")
                    config
                } catch (e: Exception) {
                    println("🔥 FIRESTORE ERROR: ${e.message}")
                    initial
                }
            }

        emitAll(firestoreFlow)
    }
        .catch { e ->
            println("🔥 FLOW ERROR: ${e.message}")
        }
        .distinctUntilChanged()
}