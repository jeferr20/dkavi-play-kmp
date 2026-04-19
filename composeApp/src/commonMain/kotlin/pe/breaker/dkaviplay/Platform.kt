package pe.breaker.dkaviplay

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform