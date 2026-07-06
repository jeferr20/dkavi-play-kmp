package pe.breaker.dkaviplay.core.di

import org.koin.dsl.module
import pe.breaker.dkaviplay.core.data.repository.*
import pe.breaker.dkaviplay.core.data.util.UserSessionManager
import pe.breaker.dkaviplay.core.domain.repository.AppConfigRepository
import pe.breaker.dkaviplay.core.domain.repository.AuthRepository
import pe.breaker.dkaviplay.core.domain.repository.JuegoRepository
import pe.breaker.dkaviplay.core.domain.repository.MesaRepository
import pe.breaker.dkaviplay.core.domain.repository.NotificationRepository
import pe.breaker.dkaviplay.core.domain.repository.PersonaRepository
import pe.breaker.dkaviplay.core.domain.repository.QuickPlayRepository
import pe.breaker.dkaviplay.core.domain.repository.ReservaRepository
import pe.breaker.dkaviplay.core.domain.repository.SedeRepository
import pe.breaker.dkaviplay.core.domain.repository.TimeRepository
import pe.breaker.dkaviplay.core.domain.repository.UbigeoRepository
import pe.breaker.dkaviplay.core.domain.repository.UsuarioRepository

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get(), get()) }
    single<UsuarioRepository> { UsuarioRepositoryImpl(get(), get(), get()) }
    single<PersonaRepository> { PersonaRepositoryImpl(get(), get()) }
    single<SedeRepository> { SedeRepositoryImpl(get(), get()) }
    single<QuickPlayRepository> { QuickPlayRepositoryImpl(get(), get()) }
    single<UbigeoRepository> { (loadResource: suspend (String) -> String) ->
        UbigeoRepositoryImpl(
            json = get(),
            loadJsonResource = loadResource
        )
    }
    single<JuegoRepository> { JuegoRepositoryImpl(get(), get()) }
    single<ReservaRepository> { ReservaRepositoryImpl(get(), get(), get()) }
    single<TimeRepository> { TimeRepositoryImpl(get()) }
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            firestore = get(),
            firebaseMessaging = get(),
            getUserUid = { get<UserSessionManager>().getUserUid() },
            get()
        )
    }
    single<AppConfigRepository> { AppConfigRepositoryImpl(get(), get()) }
    single<MesaRepository> { MesaRepositoryImpl(get()) }
}