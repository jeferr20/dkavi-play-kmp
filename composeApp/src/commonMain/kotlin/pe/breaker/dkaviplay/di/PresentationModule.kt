package pe.breaker.dkaviplay.di

import org.koin.dsl.module
import pe.breaker.dkaviplay.domain.model.inventory.TipoPremio
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
import pe.breaker.dkaviplay.presentation.screen.moneda.MonedaModel
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

val presentationModule = module {
    single { GlobalNavigationBus }
    single { GlobalUiManager(get()) }
    single { NotificationHandler(get()) }

    factory { RegisterDatosValidator() }

    factory { SplashModel(get(), get(), get(), get()) }
    factory { LoginModel(get(), get()) }
    factory { MainContainerModel(get()) }
    factory { MapTabModel(get()) }
    factory { ProfileScreenModel(get(), get(), get(), get(), get()) }
    factory { QuickPlayModel(get()) }
    factory { QuickPlayDetailModel(get()) }
    factory { RangoScreenModel(get()) }
    factory { RegisterUsuarioModel(get()) }
    factory { ArbitroModel(get(), get(), get()) }
    factory { ForgetPasswordModel(get(), get(), get()) }
    factory { ReservasModel(get(), get(), get(), get()) }

    factory { (reservaId: String) -> AceptarRetoModel(reservaId, get(), get(), get(), get()) }
    factory { (reservaUid: String) -> AcuerdoMutuoModel(reservaUid, get(), get()) }
    factory { (tipoItem: TipoPremio) -> InventarioModel(tipoItem, get(), get()) }
    factory { (sedeUid: String) ->
        RegisterReservationScreenModel(
            get(),
            get(),
            get(),
            get(),
            sedeUid
        )
    }
    factory { (reservaId: String) -> ResultadoPartidaModel(reservaId, get(), get(), get()) }
    factory { (reservaId: String) -> RetoIniciadoModel(reservaId, get(), get()) }
    factory { (usuarioUid: String?, isLogged: Boolean) ->
        RegisterDatosModel(usuarioUid, isLogged, get(), get(), get(), get(), get())
    }
    factory { MonedaModel(get(),get()) }
}