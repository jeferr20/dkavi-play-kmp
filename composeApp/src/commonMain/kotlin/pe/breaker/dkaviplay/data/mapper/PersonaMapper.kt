package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.cache.PersonaTable
import pe.breaker.dkaviplay.data.entity.PersonaEntity
import pe.breaker.dkaviplay.data.remote.firebase.PersonaFirebase


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