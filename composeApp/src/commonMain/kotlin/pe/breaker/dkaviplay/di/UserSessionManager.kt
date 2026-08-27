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
        try {
            val token = secureStorage.get(SecureKeys.TOKEN)
            val uid = secureStorage.get(SecureKeys.USERUID)
            val idStr = secureStorage.get(SecureKeys.USERID)

            // 💡 CRÍTICO: Si el storage devuelve nulo, pero ya tenemos datos en RAM
            // NO los sobrescribimos con nulo. Esto evita perder la sesión si el storage
            // tiene un retardo en iOS (Keychain latency).
            if (token != null) cachedToken = token
            if (uid != null) cachedUid = uid
            if (idStr != null) cachedId = idStr.toIntOrNull()

            cachedSupabaseToken = secureStorage.get(SecureKeys.SUPABASETOKEN) ?: cachedSupabaseToken
            cachedSupabaseRefreshToken = secureStorage.get(SecureKeys.SUPABASEREFRESHTOKEN) ?: cachedSupabaseRefreshToken
            cachedFirebaseToken = secureStorage.get(SecureKeys.FIREBASETOKEN) ?: cachedFirebaseToken

            println("DEBUG: loadSession - Token en RAM final: ${!cachedToken.isNullOrBlank()}")
        } catch (e: Exception) {
            println("❌ ERROR en UserSessionManager.loadSession: ${e.message}")
        }
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

        // 2. Actualizar almacenamiento seguro
        try {
            saveOrClear(SecureKeys.TOKEN, token)
            saveOrClear(SecureKeys.USERUID, uid)
            saveOrClear(SecureKeys.USERID, userId?.toString())
            saveOrClear(SecureKeys.FIREBASETOKEN, firebaseToken)
            saveOrClear(SecureKeys.SUPABASETOKEN, supabaseToken)
            saveOrClear(SecureKeys.SUPABASEREFRESHTOKEN, supabaseRefreshToken)
            println("DEBUG: saveSession completado físicamente")
        } catch (e: Exception) {
            println("❌ ERROR en UserSessionManager.saveSession: ${e.message}")
            e.printStackTrace()
        }
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
            val result = secureStorage.save(key, value)
            if (result.isFailure) {
                println("⚠️ ERROR persistiendo llave $key en SecureStorage: ${result.exceptionOrNull()?.message}")
                throw result.exceptionOrNull() ?: Exception("Error desconocido persistiendo $key")
            }
        } else {
            secureStorage.delete(key)
        }
    }
}