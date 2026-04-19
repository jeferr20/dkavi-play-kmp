package pe.breaker.dkaviplay.data.mapper

import pe.breaker.dkaviplay.data.entity.PersonaEntity
import pe.breaker.dkaviplay.data.remote.firebase.PersonaFirebase
import pe.breaker.dkaviplay.cache.PersonaTable

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

fun mapToPersonaFirebase(data: Map<String, Any?>): PersonaFirebase {
    return PersonaFirebase(
        personaUid = data["personaUid"] as? String,
        nombres = data["nombres"] as? String,
        apellidoPaterno = data["apellidoPaterno"] as? String,
        apellidoMaterno = data["apellidoMaterno"] as? String,
        celular = data["celular"] as? String,
        correo = data["correo"] as? String,
        codigoExpiracion = (data["codigoExpiracion"] as? Number)?.toLong(),
        codigoRecuperacion = data["codigoRecuperacion"] as? String
    )
}