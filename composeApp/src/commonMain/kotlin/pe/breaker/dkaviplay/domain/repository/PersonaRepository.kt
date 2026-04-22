package pe.breaker.dkaviplay.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.data.entity.PersonaEntity

interface PersonaRepository {
    fun getPersonaStream(personaUid: String): Flow<PersonaEntity?>
}