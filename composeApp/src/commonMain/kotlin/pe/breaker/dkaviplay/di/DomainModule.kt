package pe.breaker.dkaviplay.di

import org.koin.dsl.module
import pe.breaker.dkaviplay.domain.usecase.CheckSessionUseCase
import pe.breaker.dkaviplay.domain.usecase.DeleteAccountUseCase
import pe.breaker.dkaviplay.domain.usecase.EliminarReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.GetMesaUseCase
import pe.breaker.dkaviplay.domain.usecase.GetSedesByUbigeoUseCase
import pe.breaker.dkaviplay.domain.usecase.LogOutUseCase
import pe.breaker.dkaviplay.domain.usecase.LoginUseCase
import pe.breaker.dkaviplay.domain.usecase.ObserveAppConfigUseCase
import pe.breaker.dkaviplay.domain.usecase.RegisterPersonaUseCase
import pe.breaker.dkaviplay.domain.usecase.RegisterUseCase
import pe.breaker.dkaviplay.domain.usecase.SearchUsersQuickPlayUseCase
import pe.breaker.dkaviplay.domain.usecase.SendCodeUseCase
import pe.breaker.dkaviplay.domain.usecase.SendNotificacionUseCase
import pe.breaker.dkaviplay.domain.usecase.UpdatePasswordUseCase
import pe.breaker.dkaviplay.domain.usecase.UploadProfileImageUseCase
import pe.breaker.dkaviplay.domain.usecase.VerifyCodeUseCase
import pe.breaker.dkaviplay.domain.usecase.moneda.GetMensajeCompraUseCase
import pe.breaker.dkaviplay.domain.usecase.moneda.GetTarifaMonedaUseCase
import pe.breaker.dkaviplay.domain.usecase.moneda.InsertMonedaLogUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.GetReservasUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.GetReservationByIdUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.RefreshReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.RegisterReservationUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.ResponserRetoUseCase
import pe.breaker.dkaviplay.domain.usecase.reserva.ValidarHoraReservaUseCase
import pe.breaker.dkaviplay.domain.usecase.usuarioQuickPlay.GetUsuarioQuickPlayUseCase

val domainModule = module {
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { RegisterPersonaUseCase(get()) }
    factory { SendCodeUseCase(get()) }
    factory { VerifyCodeUseCase(get()) }
    factory { UpdatePasswordUseCase(get()) }
    factory { RegisterReservationUseCase(get()) }
    factory { GetReservationByIdUseCase(get()) }
    factory { ResponserRetoUseCase(get()) }
    factory { LogOutUseCase(get()) }
    factory { DeleteAccountUseCase(get()) }
    factory { CheckSessionUseCase(get(),get(),get(),get(),get()) }
    factory { GetReservasUseCase(get()) }
    factory { RefreshReservaUseCase(get()) }
    factory { EliminarReservaUseCase(get()) }
    factory { GetMesaUseCase(get()) }
    factory { UploadProfileImageUseCase(get()) }
    factory { SearchUsersQuickPlayUseCase(get()) }
    factory { GetUsuarioQuickPlayUseCase(get()) }
    factory { GetSedesByUbigeoUseCase(get()) }
    factory { ObserveAppConfigUseCase(get()) }
    factory { SendNotificacionUseCase(get()) }
    factory { GetMensajeCompraUseCase(get()) }
    factory { GetTarifaMonedaUseCase(get()) }
    factory { InsertMonedaLogUseCase(get()) }
    factory { ValidarHoraReservaUseCase(get()) }
}