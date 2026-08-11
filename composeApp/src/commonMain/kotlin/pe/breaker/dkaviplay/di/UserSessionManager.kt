package pe.breaker.dkaviplay.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
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
    private var cachedSupabaseRefreshToken: String? = null
    private var cachedFirebaseToken: String? = null

    /**
     * Carga de forma asíncrona los tokens almacenados en el almacenamiento seguro.
     */
    suspend fun loadSession() {
        cachedToken = secureStorage.get(SecureKeys.TOKEN)
        cachedUid = secureStorage.get(SecureKeys.USERUID)
        cachedId = secureStorage.get(SecureKeys.USERID)?.toInt()
        cachedSupabaseToken = secureStorage.get(SecureKeys.SUPABASETOKEN)
        cachedSupabaseRefreshToken = secureStorage.get(SecureKeys.SUPABASEREFRESHTOKEN)
        cachedFirebaseToken = secureStorage.get(SecureKeys.FIREBASETOKEN)
    }

    fun getToken(): String? = cachedToken
    fun getUserUid(): String? = cachedUid
    fun getUserId(): Int? = cachedId
    fun getSupabaseToken(): String? = cachedSupabaseToken
    fun getSupabaseRefreshToken(): String? = cachedSupabaseRefreshToken
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
        supabaseToken: String?,
        supabaseRefreshToken: String?
    ) {
        // 1. Actualizar memoria RAM
        cachedToken = token
        cachedUid = uid
        cachedId = userId
        cachedFirebaseToken = firebaseToken
        cachedSupabaseToken = supabaseToken
        cachedSupabaseRefreshToken = supabaseRefreshToken

        // 2. Actualizar almacenamiento seguro (Sobrescribir o borrar si es null)
        saveOrClear(SecureKeys.TOKEN, token)
        saveOrClear(SecureKeys.USERUID, uid)
        saveOrClear(SecureKeys.USERID, userId?.toString())
        saveOrClear(SecureKeys.FIREBASETOKEN, firebaseToken)
        saveOrClear(SecureKeys.SUPABASETOKEN, supabaseToken)
        saveOrClear(SecureKeys.SUPABASEREFRESHTOKEN, supabaseRefreshToken)
    }

    /**
     * Elimina por completo los tokens del servidor, borra el almacenamiento seguro
     * y vacía las tablas de caché locales de SQLite.
     */
    suspend fun clearSession() {
        // 1. Intentar borrar el token en el servidor con un tiempo límite estricto
        try {
            kotlinx.coroutines.withTimeout(2000) { // Máximo 2 segundos de espera
                notificationRepository.deleteToken()
            }
        } catch (e: Exception) {
            println("Error o Timeout al eliminar token de notificaciones en el servidor: ${e.message}")
        }

        // 2. Limpieza de estados en memoria (Instantáneo)
        cachedToken = null
        cachedUid = null
        cachedId = null
        cachedFirebaseToken = null
        cachedSupabaseToken = null
        cachedSupabaseRefreshToken = null

        // 3. Limpieza de persistencia segura y base de datos local (Garantizado)
        secureStorage.clearAll()

        // Asegúrate de ejecutar las limpiezas de la base de datos relacional en el hilo correcto de I/O
        withContext(Dispatchers.IO) {
            try {
                database.clearUsuarioTable()
                database.clearInventarioTable() // Ojo con el typo: verifica si es clearInventarioTable
                database.clearHorarioTable()
            } catch (e: Exception) {
                println("Error limpiando tablas SQLite locales: ${e.message}")
            }
        }
    }

    private suspend fun saveOrClear(key: String, value: String?) {
        if (value != null) {
            secureStorage.save(key, value)
        } else {
            secureStorage.delete(key)
        }
    }
}