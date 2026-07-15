package pe.breaker.dkaviplay.util

fun obtenerCoordenadasPorUbigeo(departamentoId: String?): Pair<Double, Double>? {
    return when (departamentoId) {
        "2534" -> Pair(-6.2294, -77.8714)  // Amazonas (Chachapoyas)
        "2625" -> Pair(-9.5261, -77.5289)  // Ancash (Huaraz)
        "2812" -> Pair(-13.6339, -72.8814) // Apurimac (Abancay)
        "2900" -> Pair(-16.4090, -71.5374) // Arequipa
        "3020" -> Pair(-13.1588, -74.2239) // Ayacucho
        "3143" -> Pair(-7.1638, -78.5003)  // Cajamarca
        "3292" -> Pair(-13.5320, -71.9675) // Cusco
        "3414" -> Pair(-12.7826, -74.9727) // Huancavelica
        "3518" -> Pair(-9.9306, -76.2422)  // Huanuco
        "3606" -> Pair(-14.0678, -75.7286) // Ica
        "3655" -> Pair(-12.0651, -75.2049) // Junin (Huancayo)
        "3788" -> Pair(-8.1160, -79.0300)  // La Libertad (Trujillo)
        "3884" -> Pair(-6.7714, -79.8406)  // Lambayeque (Chiclayo)
        "3926" -> Pair(-12.0464, -77.0428) // Lima
        "4108" -> Pair(-3.7437, -73.2516)  // Loreto (Iquitos)
        "4165" -> Pair(-12.5933, -69.1833) // Madre de Dios (Puerto Maldonado)
        "4180" -> Pair(-17.1983, -70.9356) // Moquegua
        "4204" -> Pair(-10.6675, -76.2561) // Pasco (Cerro de Pasco)
        "4236" -> Pair(-5.1945, -80.6328)  // Piura
        "4309" -> Pair(-15.8422, -70.0199) // Puno
        "4431" -> Pair(-6.4899, -76.3727)  // San Martin (Moyobamba)
        "4519" -> Pair(-18.0066, -70.2462) // Tacna
        "4551" -> Pair(-3.5669, -80.4514)  // Tumbes
        "4567" -> Pair(-8.3791, -74.5539)  // Ucayali (Pucallpa)
        else -> null
    }
}