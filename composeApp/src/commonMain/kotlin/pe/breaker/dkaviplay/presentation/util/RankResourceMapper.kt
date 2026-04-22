package pe.breaker.dkaviplay.presentation.util

import dkaviplay.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import dkaviplay.composeapp.generated.resources.Aprendiz
import dkaviplay.composeapp.generated.resources.Arcángel
import dkaviplay.composeapp.generated.resources.Asesino
import dkaviplay.composeapp.generated.resources.Bronce
import dkaviplay.composeapp.generated.resources.Centinela
import dkaviplay.composeapp.generated.resources.Conquistador
import dkaviplay.composeapp.generated.resources.Constructor
import dkaviplay.composeapp.generated.resources.Destructor
import dkaviplay.composeapp.generated.resources.Devastador
import dkaviplay.composeapp.generated.resources.Diamante
import dkaviplay.composeapp.generated.resources.Emperador
import dkaviplay.composeapp.generated.resources.GranMaestro
import dkaviplay.composeapp.generated.resources.Guerrero
import dkaviplay.composeapp.generated.resources.Infernal
import dkaviplay.composeapp.generated.resources.Inmortal
import dkaviplay.composeapp.generated.resources.Jinete
import dkaviplay.composeapp.generated.resources.Kripton
import dkaviplay.composeapp.generated.resources.Maestro
import dkaviplay.composeapp.generated.resources.Novato
import dkaviplay.composeapp.generated.resources.Oro
import dkaviplay.composeapp.generated.resources.Plata
import dkaviplay.composeapp.generated.resources.Platino
import dkaviplay.composeapp.generated.resources.Recluta
import dkaviplay.composeapp.generated.resources.Roca
import dkaviplay.composeapp.generated.resources.Samurai

object RankResourceMapper {
    @OptIn(InternalResourceApi::class)
    fun getDrawableByRank(rango: String?): DrawableResource {
        return when (rango) {
            "Novato" -> Res.drawable.Novato
            "Recluta" -> Res.drawable.Recluta
            "Aprendiz" -> Res.drawable.Aprendiz
            "Jinete" -> Res.drawable.Jinete
            "Constructor" -> Res.drawable.Constructor
            "Centinela" -> Res.drawable.Centinela
            "Roca" -> Res.drawable.Roca
            "Guerrero" -> Res.drawable.Guerrero
            "Samurái" -> Res.drawable.Samurai
            "Asesino" -> Res.drawable.Asesino
            "Bronce" -> Res.drawable.Bronce
            "Plata" -> Res.drawable.Plata
            "Oro" -> Res.drawable.Oro
            "Platino" -> Res.drawable.Platino
            "Diamante" -> Res.drawable.Diamante
            "Kriptón" -> Res.drawable.Kripton
            "Maestro" -> Res.drawable.Maestro
            "Gran Maestro" -> Res.drawable.GranMaestro
            "Destructor" -> Res.drawable.Destructor
            "Devastador" -> Res.drawable.Devastador
            "Conquistador" -> Res.drawable.Conquistador
            "Emperador" -> Res.drawable.Emperador
            "Arcángel" -> Res.drawable.Arcángel
            "Infernal" -> Res.drawable.Infernal
            "Inmortal" -> Res.drawable.Inmortal
            else -> Res.drawable.Novato
        }
    }
}