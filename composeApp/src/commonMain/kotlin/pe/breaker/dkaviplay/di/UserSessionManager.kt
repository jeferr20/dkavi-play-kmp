package pe.breaker.dkaviplay.di

import pe.breaker.dkaviplay.cache.PersonaTable
import pe.breaker.dkaviplay.cache.UsuarioTable
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.domain.repository.NotificationRepository
import pe.breaker.dkaviplay.util.SecureKeys
import pe.breaker.dkaviplay.util.SecureStorage

/**
 * Gestiona única y exclusivamente el ciclo de vida de las credenciales de la sesión local,
 * persistencia segura en el dispositivo y la limpieza de datos en caché.
 */
class UserSessionManager(
    private val notificationRepository: NotificationRepository,
    private val secureStorage: SecureStorage,
    private val database: Database
) {
    private var cachedToken: String? = null
    private var cachedUid: String? = null

    /**
     * Carga de forma asíncrona los tokens almacenados en el almacenamiento seguro.
     */
    suspend fun loadSession() {
        cachedToken = secureStorage.get(SecureKeys.TOKEN)
        cachedUid = secureStorage.get(SecureKeys.USERUID)
    }

    fun getToken(): String? = cachedToken
    fun getUserUid(): String? = cachedUid

    // Getters directos de la base de datos local para la sesión activa
    fun getCurrentPersona(): PersonaTable? = database.getPersonaTable()
    fun getCurrentUsuario(): UsuarioTable? = database.getUsuarioTable()

    // Flows reactivos para observar el estado local del usuario desde la UI
    fun getCurrentPersonaFlow() = database.getCurrentPersonaFlow()
    fun getCurrentUsuarioFlow() = database.getCurrentUsuarioFlow()

    /**
     * Almacena las credenciales tanto en memoria como en almacenamiento persistente seguro.
     */
    suspend fun saveSession(token: String?, uid: String?) {
        if (token != null) {
            cachedToken = token
            secureStorage.save(SecureKeys.TOKEN, token)
        }
        if (uid != null) {
            cachedUid = uid
            secureStorage.save(SecureKeys.USERUID, uid)
        }
    }

    /**
     * Elimina por completo los tokens del servidor, borra el almacenamiento seguro
     * y vacía las tablas de caché locales de SQLite.
     */
    suspend fun clearSession() {
        try {
            notificationRepository.deleteToken()
        } catch (e: Exception) {
            println("Error al eliminar token de notificaciones en el servidor: ${e.message}")
        }

        // Limpieza de estados en memoria
        cachedToken = null
        cachedUid = null

        // Limpieza de persistencia segura y base de datos local
        secureStorage.clearAll()
        database.clearPersonaTable()
        database.clearUsuarioTable()
    }
}