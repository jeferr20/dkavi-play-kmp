package pe.breaker.dkaviplay.di

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.messaging.messaging
import dev.gitlive.firebase.remoteconfig.remoteConfig
import dev.gitlive.firebase.storage.storage
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.core.di.provideHttpClient
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.repository.AppConfigRepositoryImpl
import pe.breaker.dkaviplay.data.repository.AuthRepositoryImpl
import pe.breaker.dkaviplay.data.repository.JuegoRepositoryImpl
import pe.breaker.dkaviplay.data.repository.MesaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.NotificationRepositoryImpl
import pe.breaker.dkaviplay.data.repository.PersonaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.QuickPlayRepositoryImpl
import pe.breaker.dkaviplay.data.repository.ReservaRepositoryImpl
import pe.breaker.dkaviplay.data.repository.SedeRepositoryImpl
import pe.breaker.dkaviplay.data.repository.TimeRepositoryImpl
import pe.breaker.dkaviplay.data.repository.UbigeoRepositoryImpl
import pe.breaker.dkaviplay.data.repository.UsuarioRepositoryImpl
import pe.breaker.dkaviplay.core.domain.model.inventory.TipoPremio
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
import pe.breaker.dkaviplay.core.domain.usecase.EliminarReservaUseCase
import pe.breaker.dkaviplay.core.domain.usecase.GetMesaUseCase
import pe.breaker.dkaviplay.core.domain.usecase.GetReservasUseCase
import pe.breaker.dkaviplay.core.domain.usecase.GetSedesByUbigeoUseCase
import pe.breaker.dkaviplay.core.domain.usecase.GetUsuarioQuickPlayUseCase
import pe.breaker.dkaviplay.core.domain.usecase.LoginUseCase
import pe.breaker.dkaviplay.core.domain.usecase.ObserveAppConfigUseCase
import pe.breaker.dkaviplay.core.domain.usecase.RegisterPersonaUseCase
import pe.breaker.dkaviplay.core.domain.usecase.RegisterReservationUseCase
import pe.breaker.dkaviplay.core.domain.usecase.RegisterUseCase
import pe.breaker.dkaviplay.core.domain.usecase.SearchUsersQuickPlayUseCase
import pe.breaker.dkaviplay.core.domain.usecase.SendCodeUseCase
import pe.breaker.dkaviplay.core.domain.usecase.SendNotificacionUseCase
import pe.breaker.dkaviplay.core.domain.usecase.UpdatePasswordUseCase
import pe.breaker.dkaviplay.core.domain.usecase.UploadProfileImageUseCase
import pe.breaker.dkaviplay.core.domain.usecase.VerifyCodeUseCase
import pe.breaker.dkaviplay.presentation.navigation.GlobalNavigationBus
import pe.breaker.dkaviplay.presentation.navigation.NotificationHandler
import pe.breaker.dkaviplay.presentation.screen.aceptarReto.AceptarRetoModel
import pe.breaker.dkaviplay.presentation.screen.acuerdoMutuo.AcuerdoMutuoModel
import pe.breaker.dkaviplay.presentation.screen.arbitro.ArbitroModel
import pe.breaker.dkaviplay.presentation.screen.forgetPassword.ForgetPasswordModel
import pe.breaker.dkaviplay.presentation.screen.inventario.InventarioModel
import pe.breaker.dkaviplay.presentation.screen.login.LoginModel
import pe.breaker.dkaviplay.presentation.screen.mainContainer.MainContainerModel
import pe.breaker.dkaviplay.presentation.screen.map.MapTabModel
import pe.breaker.dkaviplay.presentation.screen.perfil.ProfileScreenModel
import pe.breaker.dkaviplay.presentation.screen.quickPlay.QuickPlayModel
import pe.breaker.dkaviplay.presentation.screen.quickPlayDetail.QuickPlayDetailModel
import pe.breaker.dkaviplay.presentation.screen.rangos.RangoScreenModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosModel
import pe.breaker.dkaviplay.presentation.screen.registerDatos.RegisterDatosValidator
import pe.breaker.dkaviplay.presentation.screen.registerReserva.RegisterReservationScreenModel
import pe.breaker.dkaviplay.presentation.screen.registerUsuario.RegisterUsuarioModel
import pe.breaker.dkaviplay.presentation.screen.reservas.ReservasModel
import pe.breaker.dkaviplay.presentation.screen.resultadosPartida.ResultadoPartidaModel
import pe.breaker.dkaviplay.presentation.screen.retoIniciado.RetoIniciadoModel
import pe.breaker.dkaviplay.presentation.screen.splash.SplashModel
import pe.breaker.dkaviplay.util.GlobalUiManager
import pe.breaker.dkaviplay.util.SecureStorage

val commonModule = module {
    //UseCase
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { RegisterPersonaUseCase(get()) }
    factory { SendCodeUseCase(get()) }
    factory { VerifyCodeUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
    factory { RegisterReservationUseCase(get()) }
//    factory { GetTorneosUseCase(get()) }
//    factory { InscripcionTorneoUseCase(get()) }
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
    single { AppConfigManager(get()) }
    single { GlobalUiManager(get()) }

    //MODEL
    factory { SplashModel(get(), get(), get()) }
    factory { (reservaId: String) ->
        AceptarRetoModel(reservaId, get(), get(), get(), get())
    }
    factory { (reservaUid: String) ->
        AcuerdoMutuoModel(reservaUid, get(), get())
    }
    factory { ArbitroModel(get(), get(), get()) }
    factory { ForgetPasswordModel(get(), get(), get()) }
    factory { (tipoItem: TipoPremio) ->
        InventarioModel(tipoItem, get(), get())
    }
    factory { LoginModel(get(),get()) }
    factory { MainContainerModel(get()) }
    factory { MapTabModel(get()) }
    factory { ProfileScreenModel(get(), get(), get(), get()) }
    factory { QuickPlayModel(get()) }
    factory { QuickPlayDetailModel(get()) }
    factory { RangoScreenModel(get()) }
    factory { (usuarioUid: String?, isLogged: Boolean) ->
        RegisterDatosModel(usuarioUid, isLogged, get(), get(), get(), get(), get())
    }
    factory { (sedeUid: String) ->
        RegisterReservationScreenModel(get(), get(), get(), get(), sedeUid)
    }
    factory { RegisterUsuarioModel(get()) }
    factory { ReservasModel(get(), get(), get(), get()) }
    factory { (reservaId: String) ->
        ResultadoPartidaModel(reservaId, get(), get(), get())
    }
    factory { (reservaId: String) ->
        RetoIniciadoModel(reservaId, get(), get())
    }

    //Handler
    single { NotificationHandler(get()) }

    //Validators
    factory { RegisterDatosValidator() }
}