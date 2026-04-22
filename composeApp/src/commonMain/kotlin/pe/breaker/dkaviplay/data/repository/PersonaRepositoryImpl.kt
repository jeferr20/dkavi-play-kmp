package pe.breaker.dkaviplay.data.repository

import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pe.breaker.dkaviplay.data.database.Database
import pe.breaker.dkaviplay.data.entity.PersonaEntity
import pe.breaker.dkaviplay.data.mapper.toEntity
import pe.breaker.dkaviplay.data.remote.firebase.PersonaFirebase
import pe.breaker.dkaviplay.domain.repository.PersonaRepository

class PersonaRepositoryImpl(
    private val firestore: Lazy<FirebaseFirestore>,
    private val database: Database,
) : PersonaRepository {

    override fun getPersonaStream(personaUid: String): Flow<PersonaEntity?> {
        return firestore.value
            .collection("Persona")
            .document(personaUid)
            .snapshots
            .map { snapshot ->
                if (snapshot.exists) {
                    try {
                        val personaDto = snapshot.data<PersonaFirebase>()
                        val entity = personaDto
                            .copy(personaUid = snapshot.id)
                            .toEntity()

                        withContext(Dispatchers.IO) {
                            database.insertPersonaTable(entity)
                        }

                        entity
                    } catch (e: Exception) {
                        println("Error en Firestore Stream Persona Snapshot: ${e.message}")
                        null
                    }
                } else {
                    null
                }
            }
            .distinctUntilChanged()
            .catch { e ->
                println("Error en Firestore Stream Persona: ${e.message}")
                emit(null)
            }
            .flowOn(Dispatchers.IO)
    }
}