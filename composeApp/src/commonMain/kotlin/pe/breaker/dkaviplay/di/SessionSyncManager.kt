package pe.breaker.dkaviplay.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.entity.PersonaEntity
import pe.breaker.dkaviplay.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.data.remote.firebase.InventarioFirebase
import pe.breaker.dkaviplay.domain.model.GlobalEvent
import pe.breaker.dkaviplay.domain.model.inventory.PremiosRegistry
import pe.breaker.dkaviplay.domain.model.rango.RangoRegistry
import pe.breaker.dkaviplay.domain.repository.PersonaRepository
import pe.breaker.dkaviplay.domain.repository.UsuarioRepository

class SessionSyncManager(
    private val sessionManager: UserSessionManager,
    private val personaRepository: PersonaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val database: Database
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _globalEvent = MutableSharedFlow<GlobalEvent>(
        replay = 1,
        extraBufferCapacity = 10
    )
    val globalEvent = _globalEvent.asSharedFlow()

    private var personaJob: Job? = null
    private var usuarioJob: Job? = null

    /**
     * Inicia la escucha activa de los streams de Firestore para el usuario autenticado.
     */
    fun startSync() {
        val userId = sessionManager.getUserUid() ?: return

        if (usuarioJob?.isActive == true) return
        var lastUsuario: UsuarioEntity? = null

        usuarioJob = usuarioRepository.getUsuarioStream(userId)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { nuevo ->
                val anterior = lastUsuario

                if (anterior != null) {
                    handleLevelChange(anterior, nuevo)
                    handleInventoryChange(anterior, nuevo)
                }

                lastUsuario = nuevo
            }
            .launchIn(scope)

        personaJob = personaRepository.getPersonaStream(userId)
            .onEach { persona ->
                println("Persona sincronizada con éxito: ${persona?.nombres}")
            }
            .launchIn(scope)
    }

    /**
     * Cancela las suscripciones activas a los flujos de datos.
     */
    fun stopSync() {
        personaJob?.cancel()
        usuarioJob?.cancel()

        personaJob = null
        usuarioJob = null
    }

    /**
     * Expone el flujo reactivo de la Persona para ser consumido por los Casos de Uso.
     */
    fun personaFlow(): Flow<PersonaEntity?> {
        val userId = sessionManager.getUserUid() ?: return flowOf(null)
        return personaRepository.getPersonaStream(userId)
    }

    /**
     * Evalúa si el incremento de puntos ocasionó una subida de rango/nivel.
     */
    private fun handleLevelChange(old: UsuarioEntity, new: UsuarioEntity) {
        val rangoOld = RangoRegistry.obtenerRangoPorPuntos(old.puntos)
        val rangoNew = RangoRegistry.obtenerRangoPorPuntos(new.puntos)

        if (rangoNew.nivel > rangoOld.nivel) {
            scope.launch {
                val userUid = sessionManager.getUserUid()
                if (rangoNew.recompensas.isNotEmpty() && userUid != null) {
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
    private fun handleInventoryChange(old: UsuarioEntity, new: UsuarioEntity) {
        val oldInv = old.parseInventario()
        val newInv = new.parseInventario()

        if (oldInv.isEmpty()) return

        newInv.forEach { newItem ->
            val oldItem = oldInv.find { it.id == newItem.id }

            val gainedAmount = when {
                oldItem == null -> newItem.cantidad
                newItem.cantidad > oldItem.cantidad -> newItem.cantidad - oldItem.cantidad
                else -> 0
            }

            if (gainedAmount > 0) {
                scope.launch {
                    PremiosRegistry.buscarPorId(newItem.id)?.let { detalleBase ->
                        val premioGanado = detalleBase.copy(cantidad = gainedAmount)
                        _globalEvent.emit(GlobalEvent.ItemGained(premioGanado))
                    }
                }
            }
        }
    }

    /**
     * Helper extension para deserializar el inventario guardado en formato JSON String.
     */
    private fun UsuarioEntity.parseInventario(): List<InventarioFirebase> {
        return try {
            Json.decodeFromString<List<InventarioFirebase>>(this.inventarioJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
}