package pe.breaker.dkaviplay.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.repository.AuthRepositoryImpl
import pe.breaker.dkaviplay.data.repository.JuegoRepositoryImpl
import pe.breaker.dkaviplay.data.repository.NotificationRepositoryImpl
import pe.breaker.dkaviplay.data.repository.PersonaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.QuickPlayRepositoryImpl
import pe.breaker.dkaviplay.data.repository.ReservaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.SedeRepositoryImpl
import pe.breaker.dkaviplay.data.repository.TimeRepositoryImpl
import pe.breaker.dkaviplay.data.repository.UbigeoRepositoryImpl
import pe.breaker.dkaviplay.data.repository.UsuarioRepositoryImpl
import pe.breaker.dkaviplay.domain.model.inventory.TipoPremio
import pe.breaker.dkaviplay.domain.repository.AuthRepository
import pe.breaker.dkaviplay.domain.repository.JuegoRepository
import pe.breaker.dkaviplay.domain.repository.NotificationRepository
import pe.breaker.dkaviplay.domain.repository.PersonaRepository
import pe.breaker.dkaviplay.domain.repository.QuickPlayRepository
import pe.breaker.dkaviplay.domain.repository.ReservaRepository
import pe.breaker.dkaviplay.domain.repository.SedeRepository
import pe.breaker.dkaviplay.domain.repository.TimeRepository
import pe.breaker.dkaviplay.domain.repository.UbigeoRepository
import pe.breaker.dkaviplay.domain.repository.UsuarioRepository
import pe.breaker.dkaviplay.domain.usecase.GetSedesByUbigeoUseCase
import pe.breaker.dkaviplay.domain.usecase.LoginUseCase
import pe.breaker.dkaviplay.domain.usecase.RegisterPersonaUseCase
import pe.breaker.dkaviplay.domain.usecase.RegisterUseCase
import pe.breaker.dkaviplay.domain.usecase.SendCodeUseCase
import pe.breaker.dkaviplay.domain.usecase.SendNotificacionUseCase
import pe.breaker.dkaviplay.domain.usecase.UpdatePasswordUseCase
import pe.breaker.dkaviplay.domain.usecase.UploadProfileImageUseCase
import pe.breaker.dkaviplay.domain.usecase.VerifyCodeUseCase
import pe.breaker.dkaviplay.presentation.navigation.GlobalNavigationBus
import pe.breaker.dkaviplay.presentation.navigation.NotificationHandler
import pe.breaker.dkaviplay.presentation.screen.aceptarReto.AceptarRetoModel
import pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo.AcuerdoMutuoModel
import pe.breaker.dkaviplay.presentation.screen.arbitro.ArbitroModel
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordModel
import pe.breaker.dkaviplay.presentation.screen.inventario.InventarioModel
import pe.breaker.dkaviplay.presentation.screen.login.LoginModel
import pe.breaker.dkaviplay.presentation.screen.perfil.ProfileScreenModel
import pe.breaker.dkaviplay.presentation.screen.rangos.RangoScreenModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosValidator
import pe.breaker.dkaviplay.presentation.screen.registerUsuario.RegisterUsuarioModel
import pe.breaker.dkaviplay.util.SecureStorage

val commonModule = module {
    factory { Firebase.auth }
    factory { Firebase.firestore }

    single {
        SecureStorage(
            vault = get(named("vault"))
        )
    }

    single { provideHttpClient() }

    single {
        val driverFactory: DatabaseDriverFactory = get()
        AppDatabase(driverFactory.createDriver())
    }
    single { Database(get<DatabaseDriverFactory>()) }

    single { UserSessionManager(get(), get(), get(), get()) }
    single { GlobalNavigationBus }
    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }

    //Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get()) }
    single<UsuarioRepository> { UsuarioRepositoryImpl(get(), inject(), get()) }
    single<PersonaRepository> { PersonaRepositoryImpl(inject(), get()) }
    single<SedeRepository> { SedeRepositoryImpl(get(), get()) }
    single<QuickPlayRepository> { QuickPlayRepositoryImpl(get(), get()) }
    single<UbigeoRepository> { UbigeoRepositoryImpl(get()) }
    single<JuegoRepository> { JuegoRepositoryImpl(get(), get()) }
    single<ReservaRepository> { ReservaRepositoryImpl(get(), get(), get()) }
    single<TimeRepository> { TimeRepositoryImpl(get()) }
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            firestore = get(),
            getUserUid = { get<UserSessionManager>().getUserUid() },
            get()
        )
    }
    //UseCase
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { RegisterPersonaUseCase(get()) }
    factory { SendCodeUseCase(get()) }
    factory { VerifyCodeUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
//    factory { RegisterReservationUseCase(get()) }
//    factory { GetTorneosUseCase(get()) }
//    factory { InscripcionTorneoUseCase(get()) }
//    factory { GetReservasUseCase(get()) }
//    factory { EliminarReservaUseCase(get()) }
//    factory { GetMesaUseCase(get()) }
    factory { UploadProfileImageUseCase(get()) }
//    factory { SearchUsersQuickPlayUseCase(get()) }
//    factory { GetUsuarioQuickPlayUseCase(get()) }
    factory { GetSedesByUbigeoUseCase(get()) }
//    factory { ObserveAppConfigUseCase(get()) }
    factory { SendNotificacionUseCase(get()) }

    //MODEL
    factory { (reservaId: String) ->
        AceptarRetoModel(reservaId, get(), get(),get(),get())
    }
    factory { (reservaUid: String) ->
        AcuerdoMutuoModel(reservaUid, get(), get())
    }
    factory { ArbitroModel(get(), get(), get()) }
    factory { ForgetPasswordModel(get(), get(), get()) }
    factory { (tipoItem: TipoPremio) ->
        InventarioModel(tipoItem, get(), get())
    }
    factory { LoginModel(get()) }
    factory { ProfileScreenModel(get(), get(), get(), get()) }
    factory { RangoScreenModel(get()) }
    factory { (usuarioUid: String?, isLogged: Boolean) ->
        RegisterDatosModel(usuarioUid, isLogged, get(), get(), get(), get(), get())
    }
    factory { RegisterUsuarioModel(get()) }



    //Handler
    single { NotificationHandler(get()) }

    //Validators
    factory { RegisterDatosValidator() }
}