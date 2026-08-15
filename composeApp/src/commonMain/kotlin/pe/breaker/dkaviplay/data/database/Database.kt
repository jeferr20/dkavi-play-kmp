package pe.breaker.dkaviplay.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.cache.AppDatabase
import pe.breaker.dkaviplay.cache.DatabaseDriverFactory
import pe.breaker.dkaviplay.cache.HorarioTable
import pe.breaker.dkaviplay.cache.InventarioTable
import pe.breaker.dkaviplay.cache.UsuarioTable
import pe.breaker.dkaviplay.data.entity.HorarioEntity
import pe.breaker.dkaviplay.data.entity.InventarioEntity
import pe.breaker.dkaviplay.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.data.mapper.toTable

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

    fun getCurrentInventarioFlow(): Flow<List<InventarioTable>> {
        return dbQuery.getInventarioTable()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    internal fun getHorarioTable(): List<HorarioTable> {
        return dbQuery.getHorarioTable().executeAsList()
    }

    internal fun insertUsuarioTable(usuario: UsuarioEntity){
        dbQuery.insertUsuarioTable(usuario.toTable())
    }

    internal fun insertInventarioTable(inventario: List<InventarioEntity>){
        dbQuery.transaction {
            println("📂 [DB] Sincronizando inventario local. Cantidad: ${inventario.size}")
            dbQuery.removeInventarioTable()

            inventario.forEach { item ->
                dbQuery.insertInventarioTable(item.toTable())
            }
        }
    }

    internal fun insertHorarioTable(horario: List<HorarioEntity>){
        dbQuery.transaction {
            dbQuery.removeHorarioTable()

            horario.forEach { item ->
                dbQuery.insertHorarioTable(item.toTable())
            }
        }
    }

    internal fun clearUsuarioTable() {
        dbQuery.removeUsuarioTable()
    }

    internal fun clearInventarioTable() {
        dbQuery.removeInventarioTable()
    }

    internal fun clearHorarioTable() {
        dbQuery.removeHorarioTable()
    }
}