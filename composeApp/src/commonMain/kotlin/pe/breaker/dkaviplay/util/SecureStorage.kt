package pe.breaker.dkaviplay.util

import eu.anifantakis.lib.ksafe.KSafe

class SecureStorage(
    private val vault: KSafe
) {
    suspend fun save(key: String, value: String): Result<Unit> {
        return runCatching {
            vault.put(key, value)
        }
    }

    suspend fun get(key: String): String? {
        return vault.get(key,null)
    }

    suspend fun delete(key: String) {
        vault.delete(key)
    }

    suspend fun clearAll() {
        vault.clearAll()
    }
}