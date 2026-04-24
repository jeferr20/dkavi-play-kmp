package pe.breaker.dkaviplay.util

enum class PermissionStatus {
    Checking,   // Verificando inicialmente
    Granted,    // Permiso concedido
    Denied,     // Dijo que no (se puede volver a pedir)
    PermanentlyDenied // Bloqueado (hay que ir a Ajustes)
}