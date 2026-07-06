package pe.breaker.dkaviplay.core.data.mapper

import pe.breaker.dkaviplay.cache.PersonaTable
import pe.breaker.dkaviplay.core.data.entity.PersonaEntity
import pe.breaker.dkaviplay.core.data.remote.firebase.PersonaFirebase

fun PersonaFirebase.toEntity() = PersonaEntity(
    personaUid = personaUid ?: "",
    nombres = nombres ?: "",
    apellidoPaterno = apellidoPaterno ?: "",
    apellidoMaterno = apellidoMaterno ?: "",
    celular = celular ?: "",
    correo = correo ?: "",
)

fun PersonaEntity.toTable() = PersonaTable(
    personaUid = personaUid,
    nombres = nombres,
    apellidoPaterno = apellidoPaterno,
    apellidoMaterno = apellidoMaterno,
    celular = celular,
    correo = correo,
)