package pe.breaker.dkaviplay.domain.usecase

class GetMensajeCompraUseCase {
    operator fun invoke(cantidadMonedas: Int, usuario: String): String {
        val mensajeBase = "Hola, me gustaría comprar $cantidadMonedas monedas para mi cuenta de jugador."
        val identificacion = "Mi usuario es: $usuario"

        // Retornamos el texto codificado para URL de manera segura
        return "$mensajeBase\n$identificacion"
    }
}