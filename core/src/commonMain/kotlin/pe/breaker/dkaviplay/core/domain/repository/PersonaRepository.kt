package pe.breaker.dkaviplay.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.breaker.dkaviplay.core.data.entity.PersonaEntity

interface PersonaRepository {
    fun getPersonaStream(personaUid: String): Flow<PersonaEntity?>
}