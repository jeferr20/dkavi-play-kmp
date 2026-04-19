package pe.breaker.dkaviplay.di


import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.repository.AuthRepositoryImpl
import pe.breaker.dkaviplay.data.repository.TimeRepositoryImpl
import pe.breaker.dkaviplay.data.repository.UbigeoRepositoryImpl
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.TimeRepository
import pe.breaker.dkaviplay.domain.repository.UbigeoRepository
import pe.breaker.dkaviplay.domain.usecase.GetMesaUseCase
import pe.breaker.dkaviplay.domain.usecase.appConfig.ObserveAppConfigUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.LoginUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.RegisterPersonaUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.RegisterUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.SendCodeUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.UpdatePasswordUseCase
import pe.breaker.dkaviplay.domain.usecase.auth.VerifyCodeUseCase
import pe.breaker.dkaviplay.domain.usecase.notificaciones.SendNotificacionUseCase
import pe.breaker.dkaviplay.domain.usecase.perfil.UploadProfileImageUseCase
import pe.breaker.dkaviplay.domain.usecase.quickPlay.SearchUsersQuickPlayUseCase
import pe.breaker.dkaviplay.domain.usecase.quickPlayDetail.GetUsuarioQuickPlayUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.EliminarReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.GetReservasUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.RegisterReservationUseCase
import pe.breaker.dkaviplay.domain.usecase.sede.GetSedesByUbigeoUseCase
import pe.breaker.dkaviplay.domain.usecase.torneo.GetTorneosUseCase
import pe.breaker.dkaviplay.domain.usecase.torneo.InscripcionTorneoUseCase
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordModel
import pe.breaker.dkaviplay.presentation.screen.login.LoginModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosValidator
import pe.breaker.dkaviplay.presentation.screen.registerUsuario.RegisterUsuarioModel

val commonModule = module {
    single { provideHttpClient() }
    single {
        val driverFactory: DatabaseDriverFactory = get()
        AppDatabase(driverFactory.createDriver())
    }
    single { Database(get<DatabaseDriverFactory>()) }
    single {
        UserSessionManager(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    //Repository
    single<AuthRepository> {
        println("CREANDO AUTH REPOSITORY")
        AuthRepositoryImpl(
            httpClient = get(),
            settings = get(),
            firestoreProvider = get(),
            firebaseAuthProvider = get(),
//            context = get(named("appContext"))
        )
    }
//    single<SedeRepository> { SedeRepositoryImpl(get(), get()) }
//    single<TorneoRepository> { TorneoRepositoryImpl(get()) }
//    single<MesaRepository> { MesaRepositoryImpl(get()) }
//    single<QuickPlayRepository> { QuickPlayRepositoryImpl(get(), get()) }
    single<UbigeoRepository> { UbigeoRepositoryImpl(get()) }
//    single<UsuarioRepository> { UsuarioRepositoryImpl(get(),get(),get()) }
//    single<PersonaRepository> { PersonaRepositoryImpl(get(), get()) }
//    single<JuegoRepository> { JuegoRepositoryImpl(get(),get()) }
//    single<NotificationRepository> {
//        NotificationRepositoryImpl(
//            firestore = get(),
//            getUserUid = { get<UserSessionManager>().getUserUid() },
//            get()
//        )
//    }
    single <TimeRepository>{ TimeRepositoryImpl(get()) }
//    single<ReservaRepository> { ReservaRepositoryImpl(get(), get(),get ()) }

    //UseCase
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { RegisterPersonaUseCase(get()) }
    factory { SendCodeUseCase(get()) }
    factory { VerifyCodeUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
    factory { RegisterReservationUseCase(get()) }
    factory { GetTorneosUseCase(get()) }
    factory { InscripcionTorneoUseCase(get()) }
    factory { GetReservasUseCase(get()) }
    factory { EliminarReservaUseCase(get()) }
    factory { GetMesaUseCase(get()) }
    factory { UploadProfileImageUseCase(get()) }
    factory { SearchUsersQuickPlayUseCase(get()) }
    factory { GetUsuarioQuickPlayUseCase(get()) }
    factory { GetSedesByUbigeoUseCase(get()) }
    factory { ObserveAppConfigUseCase(get()) }
    factory { SendNotificacionUseCase(get()) }

    //AppConfigManager
    single {
        AppConfigManager(get())
    }

    factory {
        println("CREANDO LOGIN USECASE")
        LoginModel(get())
    }
    factory { ForgetPasswordModel(get(),get(),get()) }
    factory { RegisterUsuarioModel(get()) }
    factory { (usuarioUid: String?, isLogged: Boolean) ->
        RegisterDatosModel(usuarioUid, isLogged, get(), get(), get(), get(), get())
    }


    //Validators
    factory { RegisterDatosValidator() }
}