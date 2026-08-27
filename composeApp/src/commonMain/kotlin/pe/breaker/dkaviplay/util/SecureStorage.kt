package pe.breaker.dkaviplay.util

import com.russhwolf.settings.Settings
import eu.anifantakis.lib.ksafe.KSafe

class SecureStorage(
    private val vault: KSafe,
    private val settings: Settings
) {
    private val platform = getPlatform()

    // 💡 Detectamos si debemos evitar KSafe por problemas de enlace en iOS Simulator
    private val useFallbackByDefault = platform.isIos && platform.isSimulator

    suspend fun save(key: String, value: String): Result<Unit> {
        if (useFallbackByDefault) {
            settings.putString(key, value)
            return Result.success(Unit)
        }

        return try {
            vault.put(key, value)
            Result.success(Unit)
        } catch (e: Throwable) {
            println("⚠️ KSafe failed to save $key: ${e.message}. Using fallback storage.")
            try {
                settings.putString(key, value)
                Result.success(Unit)
            } catch (fallbackEx: Throwable) {
                Result.failure(fallbackEx)
            }
        }
    }

    suspend fun get(key: String): String? {
        if (useFallbackByDefault) {
            return settings.getStringOrNull(key)
        }

        return try {
            // Using String? allows null as default value and fixes inference issues
            vault.get<String?>(key, null)
        } catch (e: Throwable) {
            println("⚠️ KSafe failed to get $key: ${e.message}. Using fallback storage.")
            settings.getStringOrNull(key)
        }
    }

    suspend fun delete(key: String) {
        if (useFallbackByDefault) {
            settings.remove(key)
            return
        }

        try {
            vault.delete(key)
        } catch (e: Throwable) {
            println("⚠️ KSafe failed to delete $key: ${e.message}")
            settings.remove(key)
        }
    }

    suspend fun clearAll() {
        if (useFallbackByDefault) {
            settings.clear()
            return
        }

        try {
            vault.clearAll()
        } catch (e: Throwable) {
            println("⚠️ KSafe failed to clear all: ${e.message}")
            settings.clear()
        }
    }
}