package pe.breaker.dkaviplay.di

import org.koin.dsl.module
import pe.breaker.dkaviplay.data.repository.AppConfigRepositoryImpl
import pe.breaker.dkaviplay.data.repository.AuthRepositoryImpl
import pe.breaker.dkaviplay.data.repository.JuegoRepositoryImpl
import pe.breaker.dkaviplay.data.repository.MesaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.MonedaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.NotificationRepositoryImpl
import pe.breaker.dkaviplay.data.repository.QuickPlayRepositoryImpl
import pe.breaker.dkaviplay.data.repository.ReservaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.SedeRepositoryImpl
import pe.breaker.dkaviplay.data.repository.TimeRepositoryImpl
import pe.breaker.dkaviplay.data.repository.UbigeoRepositoryImpl
import pe.breaker.dkaviplay.data.repository.UsuarioRepositoryImpl
import pe.breaker.dkaviplay.domain.repository.AppConfigRepository
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.JuegoRepository
import pe.breaker.dkaviplay.domain.repository.MesaRepository
import pe.breaker.dkaviplay.domain.repository.MonedaRepository
import pe.breaker.dkaviplay.domain.repository.NotificationRepository
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository
import pe.breaker.dkaviplay.domain.repository.ReservaRepository
import pe.breaker.dkaviplay.domain.repository.SedeRepository
import pe.breaker.dkaviplay.domain.repository.TimeRepository
import pe.breaker.dkaviplay.domain.repository.UbigeoRepository
import pe.breaker.dkaviplay.domain.repository.UsuarioRepository
import pe.breaker.dkaviplay.util.DateTimeFormatter
import pe.breaker.dkaviplay.util.DateTimeFormatterImpl

val dataModule = module {
    single { UserSessionManager(get(), get(), get()) }
    single {
        SessionSyncManager(get(), get(),get())
    }
    single { AppConfigManager(get()) }
    single<DateTimeFormatter> { DateTimeFormatterImpl() }

    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get(), get(),get()) }
    single<UsuarioRepository> { UsuarioRepositoryImpl(get(), get()) }
    single<SedeRepository> { SedeRepositoryImpl(get(), get(), get()) }
    single<QuickPlayRepository> { QuickPlayRepositoryImpl(get(), get(), get()) }
    single<UbigeoRepository> { UbigeoRepositoryImpl(get()) }
    single<JuegoRepository> { JuegoRepositoryImpl(get(), get()) }
    single<ReservaRepository> { ReservaRepositoryImpl(get(), get(), get(), get()) }
    single<TimeRepository> { TimeRepositoryImpl(get()) }
    single<AppConfigRepository> { AppConfigRepositoryImpl(get(), get()) }
    single<MesaRepository> { MesaRepositoryImpl(get(), get(), get()) }
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            firebaseMessaging = get(),
            get()
        )
    }
    single<MonedaRepository>{ MonedaRepositoryImpl(get(),get(),get(),get())}
}