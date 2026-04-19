package pe.breaker.dkaviplay.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.data.entity.PersonaEntity
import pe.breaker.dkaviplay.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.data.mapper.toTable
import pe.breaker.dkaviplay.cache.PersonaTable
import pe.breaker.dkaviplay.cache.UsuarioTable

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun getUsuarioTable(): UsuarioTable? {
        return dbQuery.getUsuarioTable().executeAsOneOrNull()
    }

    fun getCurrentUsuarioFlow(): Flow<UsuarioTable?> {
        return dbQuery.getUsuarioTable()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }

    fun getCurrentPersonaFlow(): Flow<PersonaTable?> {
        return dbQuery.getPersonaTable()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }

    internal fun getPersonaTable(): PersonaTable? {
        return dbQuery.getPersonaTable().executeAsOneOrNull()
    }

    internal fun insertUsuarioTable(usuario: UsuarioEntity){
        dbQuery.insertUsuarioTable(usuario.toTable())
    }

    internal fun insertPersonaTable(persona: PersonaEntity){
        dbQuery.insertPersonaTable(persona.toTable())
    }

    internal fun clearPersonaTable() {
        dbQuery.removePersonaTable()
    }

    internal fun clearUsuarioTable() {
        dbQuery.removeUsuarioTable()
    }
}