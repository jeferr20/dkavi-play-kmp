package pe.breaker.dkaviplay.domain.model

data class AppConfig(
    val activeServiceMovil: Boolean,
    val versionMovil: String,
    val urlMovilAndroid: String,
    val urlMovilIOS: String
)