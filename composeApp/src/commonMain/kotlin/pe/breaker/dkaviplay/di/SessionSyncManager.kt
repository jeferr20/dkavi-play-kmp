package pe.breaker.dkaviplay.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.entity.UsuarioConInventario
import pe.breaker.dkaviplay.domain.model.GlobalEvent
import pe.breaker.dkaviplay.domain.model.inventory.PremiosRegistry
import pe.breaker.dkaviplay.domain.model.rango.RangoRegistry
import pe.breaker.dkaviplay.domain.repository.UsuarioRepository

class SessionSyncManager(
    private val sessionManager: UserSessionManager,
    private val usuarioRepository: UsuarioRepository,
    private val database: Database
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _globalEvent = MutableSharedFlow<GlobalEvent>(
        replay = 1,
        extraBufferCapacity = 10
    )
    val globalEvent = _globalEvent.asSharedFlow()

    private var usuarioJob: Job? = null
    private var lastUsuario: UsuarioConInventario? = null

    suspend fun awaitFirstSync(): Boolean = withContext(Dispatchers.IO) {
        val userUidAuth = sessionManager.getUserUid() ?: return@withContext false
        val userId = sessionManager.getUserId() ?: return@withContext false

        try {
            // Tomamos estrictamente la primera emisión válida que responda el WebSocket
            val primeraEmision = usuarioRepository.getUsuarioStream(userUidAuth, userId)
                .filterNotNull()
                .first() // 💡 Captura la primera ráfaga y suspende la ejecución aquí

            // Insertamos inmediatamente en la base de datos local en el hilo de I/O
            database.insertUsuarioTable(primeraEmision.usuario)
            database.insertInventarioTable(primeraEmision.inventario)
            database.insertHorarioTable(primeraEmision.horario)

            // Asignamos la memoria inicial para que el startSync() posterior conozca el estado previo
            lastUsuario = primeraEmision
            true
        } catch (e: Exception) {
            println("❌ Error en la primera sincronización crítica de datos: ${e.message}")
            false
        }
    }

    /**
     * Inicia la escucha activa de los streams de Firestore para el usuario autenticado.
     */
    fun startSync() {
        val userUidAuth = sessionManager.getUserUid() ?: return
        val userId = sessionManager.getUserId() ?: return

        if (usuarioJob?.isActive == true) return

        usuarioJob = usuarioRepository.getUsuarioStream(userUidAuth, userId)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { nuevo ->
                withContext(Dispatchers.IO) {
                    try {
                        database.insertUsuarioTable(nuevo.usuario)
                        database.insertInventarioTable(nuevo.inventario)
                        database.insertHorarioTable(nuevo.horario)
                    } catch (e: Exception) {
                        println("Error guardando datos reactivos en DB local: ${e.message}")
                    }
                }

                val anterior = lastUsuario
                if (anterior != null) {
                    handleLevelChange(anterior, nuevo)
                    handleInventoryChange(anterior, nuevo)
                } else {
                    // 💡 Si es la primera vez que recibimos datos en startSync,
                    // también comparamos con lo que había en RAM (por si el primer sync falló o fue parcial)
                    lastUsuario?.let { handleInventoryChange(it, nuevo) }
                }
                lastUsuario = nuevo
            }
            .launchIn(scope)
    }

    /**
     * Cancela las suscripciones activas a los flujos de datos.
     */
    fun stopSync() {
        usuarioJob?.cancel()
        usuarioJob = null
        lastUsuario = null
    }

    /**
     * Evalúa si el incremento de puntos ocasionó una subida de rango/nivel.
     */
    private fun handleLevelChange(old: UsuarioConInventario, new: UsuarioConInventario) {
        val rangoOld = RangoRegistry.obtenerRangoPorPuntos(old.usuario.puntos)
        val rangoNew = RangoRegistry.obtenerRangoPorPuntos(new.usuario.puntos)

        if (rangoNew.nivel > rangoOld.nivel) {
            scope.launch {
                val userUid = sessionManager.getUserUid()
                if ((rangoNew.recompensas.isNotEmpty()) && (userUid != null)) {
                    println("Procesando ${rangoNew.recompensas.size} recompensas para el nuevo rango: ${rangoNew.categoria}")
                    usuarioRepository.otorgarRecompensas(userUid, rangoNew.recompensas)
                }
                _globalEvent.emit(GlobalEvent.LevelUp(rangoNew))
            }
        }
    }

    /**
     * Detecta e identifica si se han añadido nuevos artículos al inventario JSON del usuario.
     */
    private fun handleInventoryChange(old: UsuarioConInventario, new: UsuarioConInventario) {
        val oldInv = old.inventario
        val newInv = new.inventario

        if (oldInv.isEmpty()) return

        newInv.forEach { newItem ->
            val oldItem = oldInv.find { it.articuloId == newItem.articuloId }

            val gainedAmount = when {
                oldItem == null -> newItem.cantidad
                newItem.cantidad > oldItem.cantidad -> newItem.cantidad - oldItem.cantidad
                else -> 0
            }

            if (gainedAmount > 0) {
                scope.launch {
                    PremiosRegistry.buscarPorId(newItem.articuloId.toInt())?.let { detalleBase ->
                        // 💡 IMPORTANTE: Emitimos el premio con la CANTIDAD GANADA real (diferencia), no el total
                        val premioGanado = detalleBase.copy(cantidad = gainedAmount)
                        _globalEvent.emit(GlobalEvent.ItemGained(premioGanado))
                    }
                }
            }
        }
    }
}