package pe.breaker.dkaviplay.di

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
    private var cachedId: Int? = null
    private var cachedSupabaseToken: String? = null
    private var cachedFirebaseToken: String? = null

    /**
     * Carga de forma asíncrona los tokens almacenados en el almacenamiento seguro.
     */
    suspend fun loadSession() {
        cachedToken = secureStorage.get(SecureKeys.TOKEN)
        cachedUid = secureStorage.get(SecureKeys.USERUID)
        cachedId = secureStorage.get(SecureKeys.USERID)?.toInt()
        cachedSupabaseToken = secureStorage.get(SecureKeys.SUPABASETOKEN)
        cachedFirebaseToken = secureStorage.get(SecureKeys.FIREBASETOKEN)
    }

    fun getToken(): String? = cachedToken
    fun getUserUid(): String? = cachedUid
    fun getUserId(): Int? = cachedId
    fun getSupabaseToken(): String? = cachedSupabaseToken
    fun getFirebaseToken(): String? = cachedFirebaseToken

    // Getters directos de la base de datos local para la sesión activa
    fun getCurrentUsuario(): UsuarioTable? = database.getUsuarioTable()
    fun getCurrentHorario() = database.getHorarioTable()

    // Flows reactivos para observar el estado local del usuario desde la UI
    fun getCurrentUsuarioFlow() = database.getCurrentUsuarioFlow()
    fun getCurrentInventarioFlow() = database.getCurrentInventarioFlow()

    /**
     * Almacena las credenciales tanto en memoria como en almacenamiento persistente seguro.
     */
    suspend fun saveSession(
        token: String?,
        uid: String?,
        userId: Int?,
        firebaseToken: String?,
        supabaseToken: String?
    ) {
        if (token != null) {
            cachedToken = token
            secureStorage.save(SecureKeys.TOKEN, token)
        }
        if (uid != null) {
            cachedUid = uid
            secureStorage.save(SecureKeys.USERUID, uid)
        }
        if (userId != null) {
            cachedId = userId
            secureStorage.save(SecureKeys.USERID, userId.toString())
        }
        if (firebaseToken != null) {
            cachedFirebaseToken = firebaseToken
            secureStorage.save(SecureKeys.FIREBASETOKEN, firebaseToken)
        }
        if (supabaseToken != null) {
            cachedSupabaseToken = supabaseToken
            secureStorage.save(SecureKeys.SUPABASETOKEN, supabaseToken)
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
        cachedId = null
        cachedFirebaseToken = null
        cachedSupabaseToken = null

        // Limpieza de persistencia segura y base de datos local
        secureStorage.clearAll()
        database.clearUsuarioTable()
        database.clearInventarioable()
        database.clearHorarioTable()
    }
}