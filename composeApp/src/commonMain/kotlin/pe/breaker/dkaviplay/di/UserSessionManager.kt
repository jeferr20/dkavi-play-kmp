package pe.breaker.dkaviplay.di

import com.russhwolf.settings.Settings
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
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.entity.PersonaEntity
import pe.breaker.dkaviplay.data.entity.UsuarioEntity
import pe.breaker.dkaviplay.data.remote.firebase.InventarioFirebase
import pe.breaker.dkaviplay.domain.model.GlobalEvent
import pe.breaker.dkaviplay.domain.model.inventory.PremiosRegistry
import pe.breaker.dkaviplay.domain.model.rango.RangoRegistry
import pe.breaker.dkaviplay.domain.repository.NotificationRepository
import pe.breaker.dkaviplay.domain.repository.PersonaRepository
import pe.breaker.dkaviplay.domain.repository.UsuarioRepository
import pe.breaker.dkaviplay.cache.PersonaTable
import pe.breaker.dkaviplay.cache.UsuarioTable
import kotlin.collections.emptyList

class UserSessionManager(
    private val personaRepository: PersonaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val notificationRepository: NotificationRepository,
    private val settings: Settings,
    private val database: Database
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _globalEvent = MutableSharedFlow<GlobalEvent>(replay = 0)
    val globalEvent = _globalEvent.asSharedFlow()

    private var personaJob: Job? = null
    private var usuarioJob: Job? = null

    fun getToken(): String? = settings.getStringOrNull("auth_token")
    fun getUserUid(): String? = settings.getStringOrNull("current_user_uid")
    fun getCurrentPersona() = database.getPersonaTable()
    fun getCurrentUsuario() = database.getUsuarioTable()
    fun getCurrentPersonaFlow(): Flow<PersonaTable?> = database.getCurrentPersonaFlow()
    fun getCurrentUsuarioFlow(): Flow<UsuarioTable?> = database.getCurrentUsuarioFlow()

    suspend fun clearSession() {
        try {
            notificationRepository.deleteToken()
        } catch (e: Exception) {
            println("Error al eliminar token en servidor: ${e.message}")
        }
        stopSync(clearLocalData = true)
        settings.clear()
    }

    fun startSync() {
        val userId = settings.getStringOrNull("current_user_uid") ?: return

        if (personaJob?.isActive == true && usuarioJob?.isActive == true) return

        usuarioJob = usuarioRepository.getUsuarioStream(userId)
            .filterNotNull()
            .distinctUntilChanged()
            .scan(null as UsuarioEntity?) { anterior, nuevo ->
                if(anterior!=null){
                    val rangoAnterior = RangoRegistry.obtenerRangoPorPuntos(anterior.puntos)
                    val rangoNuevo = RangoRegistry.obtenerRangoPorPuntos(nuevo.puntos)

                    // SOLO disparamos si el NIVEL cambió (ej. de 1 a 2)
                    if (rangoNuevo.nivel > rangoAnterior.nivel) {
                        emitLevelUpEvent(nuevo.puntos)
                    }
                }

                val oldInv = anterior?.parseInventario() ?: emptyList()
                val newInv = nuevo.parseInventario()
                detectNewItems(oldInv, newInv)

                nuevo
            }
            .launchIn(scope)

        personaJob = personaRepository.getPersonaStream(userId)
            .onEach { persona ->
                println("Persona sincronizada: ${persona?.nombres}")
            }
            .launchIn(scope)
    }

    fun personaFlow(): Flow<PersonaEntity?> {
        val userId = settings.getStringOrNull("current_user_uid") ?: return flowOf(null)
        return personaRepository.getPersonaStream(userId)
    }

    fun stopSync(clearLocalData: Boolean = false) {
        personaJob?.cancel()
        usuarioJob?.cancel()

        personaJob = null
        usuarioJob = null

        if (clearLocalData) {
            database.clearPersonaTable()
            database.clearUsuarioTable()
        }
    }

    private fun UsuarioEntity.parseInventario(): List<InventarioFirebase> {
        return try {
            Json.decodeFromString<List<InventarioFirebase>>(this.inventarioJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun detectNewItems(old: List<InventarioFirebase>, new: List<InventarioFirebase>) {
        if (old.isEmpty()) return

        new.forEach { newItem ->
            val oldItem = old.find { it.id == newItem.id }

            val gainedAmount = when {
                oldItem == null -> newItem.cantidad
                newItem.cantidad > oldItem.cantidad -> newItem.cantidad - oldItem.cantidad
                else -> 0
            }

            if (gainedAmount > 0) {
                emitInventoryEvent(newItem.id, gainedAmount)
            }
        }
    }

    private fun emitInventoryEvent(itemId: Int, amount: Int) {
        scope.launch {
            val detalleBase = PremiosRegistry.buscarPorId(itemId)
            if (detalleBase != null) {
                val premioGanado = detalleBase.copy(cantidad = amount)
                _globalEvent.emit(GlobalEvent.ItemGained(premioGanado))
            }
        }
    }

    private fun emitLevelUpEvent(puntosActuales: Int) {
        scope.launch {
            val rangoNuevo = RangoRegistry.obtenerRangoPorPuntos(puntosActuales)
            val userUid = getUserUid()
            if (rangoNuevo.recompensas.isNotEmpty() && userUid != null) {
                println("Procesando ${rangoNuevo.recompensas.size} recompensas para el rango: ${rangoNuevo.categoria}")

                usuarioRepository.otorgarRecompensas(userUid,rangoNuevo.recompensas)
            }
            _globalEvent.emit(GlobalEvent.LevelUp(rangoNuevo))
        }
    }
}