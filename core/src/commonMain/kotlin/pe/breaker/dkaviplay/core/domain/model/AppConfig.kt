package pe.breaker.dkaviplay.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val activeServiceMovil: Boolean = true,
    val versionMovil: String = "1.0.0",
    val urlMovilAndroid: String = "",
    val urlMovilIOS: String = ""
)